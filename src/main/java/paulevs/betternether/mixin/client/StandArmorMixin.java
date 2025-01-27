package paulevs.betternether.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.betternether.BetterNether;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
@Mixin(ArmorStandEntityRenderer.class)
public abstract class StandArmorMixin extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
	public StandArmorMixin(EntityRendererFactory.Context context, ArmorStandArmorEntityModel entityModel, float f) {
		super(context, entityModel, f);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject(method = "<init>*", at = @At(value = "RETURN"))
	private void onInit(EntityRendererFactory.Context ctx, CallbackInfo info) {
		if (BetterNether.hasThinArmor()) {
			Iterator<FeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel>> iterator = this.features.iterator();
			while (iterator.hasNext()) {
				FeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> feature = iterator.next();
				if (feature instanceof ArmorFeatureRenderer) {
					this.features.remove(feature);
					break;
				}
			}
			this.features.add(
					0,
					new ArmorFeatureRenderer(
							this,
							new ArmorStandArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR)),
							new ArmorStandArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR))
					)
			);
		}
	}
}