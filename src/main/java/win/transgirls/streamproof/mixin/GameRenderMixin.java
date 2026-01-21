package win.transgirls.streamproof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.transgirls.streamproof.Streamproof;
import win.transgirls.streamproof.systems.gl.GL;
import win.transgirls.streamproof.types.WorldRenderData;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Shadow
    @Final
    GuiRenderState guiState;

    @Shadow
    @Final
    private OrderedRenderCommandQueueImpl orderedRenderCommandQueue;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private GuiRenderer guiRenderer;

    @Shadow
    @Final
    private FogRenderer fogRenderer;

    @Shadow
    @Final
    private BufferBuilderStorage buffers;

    @WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"))
    private void wrapRenderWorld(WorldRenderer instance, ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f basicProjectionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, Operation<Void> original) {
        Streamproof.lastProjectionSlice = RenderSystem.getProjectionMatrixBuffer();
        original.call(instance, allocator, tickCounter, renderBlockOutline, camera, positionMatrix, basicProjectionMatrix, projectionMatrix, fogBuffer, fogColor, renderSky);
    }

    @WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void wrapClearDepthBuffer(CommandEncoder encoder, GpuTexture depthTexture, double v, Operation<Void> original) {
        int w = depthTexture.getWidth(0);
        int h = depthTexture.getHeight(0);

        if (Streamproof.secretDepthTex == null || Streamproof.secretDepthTex.getWidth(0) != w || Streamproof.secretDepthTex.getHeight(0) != h) {
            if (Streamproof.secretDepthTex != null) Streamproof.secretDepthTex.close();

            Streamproof.secretDepthTex = (GlTexture) RenderSystem.getDevice().createTexture(
                    "SecretDepthBuffer",
                    depthTexture.usage() | GpuTexture.USAGE_RENDER_ATTACHMENT,
                    depthTexture.getFormat(),
                    w, h, 1, 1
            );

            Streamproof.secretDepthView = null;
        }

        encoder.copyTextureToTexture(depthTexture, Streamproof.secretDepthTex, 0, 0, 0, 0, 0, w, h);
        original.call(encoder, depthTexture, v);
    }

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void injectRender(RenderTickCounter deltaTracker, boolean bl, CallbackInfo ci) {
        if (Streamproof.obsWrapper != null && Streamproof.obsWrapper.hooked) {
            if (Streamproof.secretDepthTex != null) {
                Streamproof.renderWorldSecrets = () -> {
                    Profiler profiler = Profilers.get();

                    RenderSystem.setProjectionMatrix(Streamproof.lastProjectionSlice, ProjectionType.PERSPECTIVE);

                    profiler.push("streamproofWorldBufferCopy");

                    MatrixStack stack = new MatrixStack();
                    VertexConsumerProvider.Immediate buffers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

                    stack.push();
                    stack.peek().rotate(Streamproof.client.gameRenderer.getCamera().getRotation().conjugate(new Quaternionf()));

                    Framebuffer mainFbo = MinecraftClient.getInstance().getFramebuffer();
                    GpuTexture realDepth = mainFbo.depthAttachment;

                    if (Streamproof.secretDepthView == null) {
                        Streamproof.secretDepthView = RenderSystem.getDevice().createTextureView(Streamproof.secretDepthTex);
                    }

                    int w = Streamproof.secretDepthTex.getWidth(0);
                    int h = Streamproof.secretDepthTex.getHeight(0);

                    if (realDepth != null && w <= realDepth.getWidth(0) && h <= realDepth.getHeight(0)) {
                        RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(
                                Streamproof.secretDepthTex, realDepth,
                                0, 0, 0, 0, 0, w, h
                        );
                    }

                    profiler.pop();

                    profiler.push("streamproofWorldRendering");

                    GL.enableDepth();
                    GL.depthFunc(GL11C.GL_LEQUAL);

                    Streamproof.renderQueue.releaseDeferredWorld(stack, buffers);
                    buffers.draw();

                    GL.disableDepth();
                    stack.pop();
                };
            }

            Streamproof.renderGuiSecrets = () -> {
                Profiler profiler = Profilers.get();
                Framebuffer renderTarget = this.client.getFramebuffer();

                this.guiState.clear();

                int i = (int) this.client.mouse.getScaledX(this.client.getWindow());
                int j = (int) this.client.mouse.getScaledY(this.client.getWindow());

                profiler.push("streamproofGuiRendering");

                DrawContext graphics = new DrawContext(this.client, this.guiState, i, j);

                this.guiState.clear();
                Streamproof.renderQueue.releaseDeferredGui(graphics);

                this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
                this.guiRenderer.incrementFrame();

                profiler.pop();

                this.orderedRenderCommandQueue.onNextFrame();

                profiler.push("streamproofBlit");
                if (!this.client.getWindow().hasZeroWidthOrHeight()) {
                    renderTarget.blitToScreen();
                }
                profiler.pop();
            };
        }
    }
}