package win.transgirls.streamproof.tools;

import net.minecraft.client.render.VertexConsumerProvider;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;

public final class ImmediatelyFastCompat {
    public static boolean forceDraw(VertexConsumerProvider consumer) {
        if (consumer instanceof BatchableBufferSource batchable) {
            batchable.draw();
            return true;
        }

        return false;
    }
}
