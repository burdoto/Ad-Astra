package earth.terrarium.ad_astra.client.renderer.entity.vehicles.rover;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.ad_astra.client.renderer.entity.vehicles.VehicleEntityRenderer;
import earth.terrarium.ad_astra.entities.vehicles.RoverEntity;
import earth.terrarium.ad_astra.util.ModResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class RoverEntityRenderer extends VehicleEntityRenderer<RoverEntity, RoverEntityModel> {
    public static final ResourceLocation TEXTURE = new ModResourceLocation("textures/vehicles/tier_1_rover.png");

    public RoverEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new RoverEntityModel(context.bakeLayer(RoverEntityModel.LAYER_LOCATION)), 1.0f);
    }

    @Override
    public void render(RoverEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(RoverEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(RoverEntity entity, Frustum frustum, double x, double y, double z) {
        return frustum.isVisible(entity.getBoundingBox().inflate(4));
    }
}