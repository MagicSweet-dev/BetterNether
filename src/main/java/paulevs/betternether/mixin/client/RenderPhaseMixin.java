package paulevs.betternether.mixin.client;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderPhase.class)
public class RenderPhaseMixin {
	@Shadow
	@Final
	protected static RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY;

	@Shadow
	@Final
	protected static RenderPhase.WriteMaskState COLOR_MASK;

	/*@Shadow
	@Final
	protected static RenderPhase.Fog FOG;*/

	@Shadow
	@Final
	protected static RenderPhase.DepthTest LEQUAL_DEPTH_TEST;

	@Shadow
	@Final
	protected static RenderPhase.Layering POLYGON_OFFSET_LAYERING;

	@Shadow
	@Final
	protected static RenderPhase.Target CLOUDS_TARGET;

	static {

	}
}