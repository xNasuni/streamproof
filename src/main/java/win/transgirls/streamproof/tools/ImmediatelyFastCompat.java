package win.transgirls.streamproof.tools;

import net.minecraft.client.renderer.MultiBufferSource;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;

public final class ImmediatelyFastCompat {
    public static boolean forceDraw(MultiBufferSource consumer) {
        if (consumer instanceof BatchableBufferSource batchable) {
            batchable.endBatch();
            return true;
        }

        return false;
    }
}
