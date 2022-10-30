package earth.terrarium.ad_astra.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import earth.terrarium.ad_astra.client.registry.ClientModKeybindings;
import earth.terrarium.ad_astra.client.registry.ClientModParticles;
import earth.terrarium.ad_astra.client.registry.ClientModScreens;
import earth.terrarium.ad_astra.client.renderer.armour.ArmourRenderers;
import earth.terrarium.ad_astra.client.renderer.block.ChestItemRenderer;
import earth.terrarium.ad_astra.client.renderer.block.LaunchPadBlockEntityRenderer;
import earth.terrarium.ad_astra.client.renderer.block.SlidingDoorBlockEntityRenderer;
import earth.terrarium.ad_astra.client.renderer.block.flag.FlagItemRenderer;
import earth.terrarium.ad_astra.client.renderer.block.globe.GlobeItemRenderer;
import earth.terrarium.ad_astra.client.renderer.entity.vehicles.rockets.tier_1.RocketItemRendererTier1;
import earth.terrarium.ad_astra.client.renderer.entity.vehicles.rockets.tier_2.RocketItemRendererTier2;
import earth.terrarium.ad_astra.client.renderer.entity.vehicles.rockets.tier_3.RocketItemRendererTier3;
import earth.terrarium.ad_astra.client.renderer.entity.vehicles.rockets.tier_4.RocketItemRendererTier4;
import earth.terrarium.ad_astra.client.renderer.entity.vehicles.rover.RoverItemRenderer;
import earth.terrarium.ad_astra.client.resourcepack.*;
import earth.terrarium.ad_astra.client.screens.PlayerOverlayScreen;
import earth.terrarium.ad_astra.data.Planet;
import earth.terrarium.ad_astra.registry.ModBlocks;
import earth.terrarium.ad_astra.registry.ModFluids;
import earth.terrarium.ad_astra.registry.ModItems;
import earth.terrarium.ad_astra.util.ModResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class AdAstraClient {

    public static List<Planet> planets = new ArrayList<>();
    public static List<SolarSystem> solarSystems = new ArrayList<>();
    public static List<PlanetSkyRenderer> skyRenderers = new ArrayList<>();
    public static List<PlanetRing> planetRings = new ArrayList<>();
    public static List<Galaxy> galaxies = new ArrayList<>();

    public static void initializeClient() {
        ClientModScreens.register();
        ClientModParticles.register();
        ClientModKeybindings.register();
        ClientGuiEvent.RENDER_HUD.register(PlayerOverlayScreen::render);

        ArmourRenderers.register();

        RenderTypeRegistry.register(RenderType.cutout(), ModBlocks.WATER_PUMP.get(), ModBlocks.ENERGIZER.get(), ModBlocks.STEEL_DOOR.get(), ModBlocks.GLACIAN_DOOR.get(), ModBlocks.GLACIAN_TRAPDOOR.get(), ModBlocks.AERONOS_DOOR.get(), ModBlocks.AERONOS_TRAPDOOR.get(), ModBlocks.STROPHAR_DOOR.get(), ModBlocks.STROPHAR_TRAPDOOR.get(), ModBlocks.EXTINGUISHED_TORCH.get(), ModBlocks.WALL_EXTINGUISHED_TORCH.get(), ModBlocks.STEEL_TRAPDOOR.get());
        RenderTypeRegistry.register(RenderType.translucent(), ModBlocks.EXTINGUISHED_LANTERN.get(), ModBlocks.GLACIAN_LEAVES.get());
        RenderTypeRegistry.register(RenderType.cutout(), ModBlocks.NASA_WORKBENCH.get(), ModBlocks.AERONOS_MUSHROOM.get(), ModBlocks.STROPHAR_MUSHROOM.get(), ModBlocks.AERONOS_LADDER.get(), ModBlocks.STROPHAR_LADDER.get(), ModBlocks.AERONOS_CHEST.get(), ModBlocks.STROPHAR_CHEST.get());

        // Sign textures
        Sheets.SIGN_MATERIALS.put(ModBlocks.GLACIAN, new Material(Sheets.SIGN_SHEET, new ModResourceLocation("entity/signs/glacian")));

        // Fluids
        RenderTypeRegistry.register(RenderType.translucent(), ModFluids.FUEL_STILL.get(), ModFluids.FLOWING_FUEL.get());
        RenderTypeRegistry.register(RenderType.translucent(), ModFluids.CRYO_FUEL_STILL.get(), ModFluids.FLOWING_CRYO_FUEL.get());
        RenderTypeRegistry.register(RenderType.translucent(), ModFluids.OIL_STILL.get(), ModFluids.FLOWING_OIL.get());
        RenderTypeRegistry.register(RenderType.translucent(), ModFluids.OXYGEN_STILL.get());
    }

    public static void onRegisterItemRenderers(BiConsumer<ItemLike, BlockEntityWithoutLevelRenderer> register) {
        register.accept(ModItems.TIER_1_ROCKET.get(), new RocketItemRendererTier1());
        register.accept(ModItems.TIER_2_ROCKET.get(), new RocketItemRendererTier2());
        register.accept(ModItems.TIER_3_ROCKET.get(), new RocketItemRendererTier3());
        register.accept(ModItems.TIER_4_ROCKET.get(), new RocketItemRendererTier4());
        register.accept(ModItems.TIER_1_ROVER.get(), new RoverItemRenderer());

        register.accept(ModBlocks.AERONOS_CHEST.get(), new ChestItemRenderer(ModBlocks.AERONOS_CHEST.get()));
        register.accept(ModBlocks.STROPHAR_CHEST.get(), new ChestItemRenderer(ModBlocks.STROPHAR_CHEST.get()));

        for (Item item : new Item[]{ModItems.EARTH_GLOBE.get(), ModItems.MOON_GLOBE.get(), ModItems.MARS_GLOBE.get(), ModItems.MERCURY_GLOBE.get(), ModItems.VENUS_GLOBE.get(), ModItems.GLACIO_GLOBE.get()}) {
            register.accept(item, new GlobeItemRenderer());
        }

        for (Item item : new Item[]{ModItems.WHITE_FLAG.get(), ModItems.BLACK_FLAG.get(), ModItems.BLUE_FLAG.get(), ModItems.BROWN_FLAG.get(), ModItems.CYAN_FLAG.get(), ModItems.GRAY_FLAG.get(), ModItems.GREEN_FLAG.get(), ModItems.LIGHT_BLUE_FLAG.get(), ModItems.LIGHT_GRAY_FLAG.get(), ModItems.LIME_FLAG.get(), ModItems.MAGENTA_FLAG.get(), ModItems.ORANGE_FLAG.get(), ModItems.PINK_FLAG.get(), ModItems.PURPLE_FLAG.get(), ModItems.RED_FLAG.get(), ModItems.YELLOW_FLAG.get()}) {
            register.accept(item, new FlagItemRenderer());
        }

        register.accept(ModItems.LAUNCH_PAD.get(), new LaunchPadBlockEntityRenderer());
    }

    public static void onRegisterReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> registry) {
        registry.accept(new ModResourceLocation("planet_resources"), new PlanetResources());
    }

    public static void onRegisterChestSprites(Consumer<ResourceLocation> register) {
        register.accept(new ModResourceLocation("entity/chest/aeronos_chest"));
        register.accept(new ModResourceLocation("entity/chest/aeronos_chest_right"));
        register.accept(new ModResourceLocation("entity/chest/aeronos_chest_left"));
        register.accept(new ModResourceLocation("entity/chest/strophar_chest"));
        register.accept(new ModResourceLocation("entity/chest/strophar_chest_right"));
        register.accept(new ModResourceLocation("entity/chest/strophar_chest_left"));
    }

    public static void onRegisterSprites(Consumer<ResourceLocation> register) {
        register.accept(new ModResourceLocation("particle/flame_1"));
        register.accept(new ModResourceLocation("particle/flame_2"));
        register.accept(new ModResourceLocation("particle/flame_3"));
        register.accept(new ModResourceLocation("particle/flame_4"));
        register.accept(new ModResourceLocation("particle/venus_rain_1"));
        register.accept(new ModResourceLocation("particle/venus_rain_2"));
        register.accept(new ModResourceLocation("particle/venus_rain_3"));
        register.accept(new ModResourceLocation("particle/venus_rain_4"));
    }

    public static void onRegisterModels(Consumer<ResourceLocation> register) {
        for (Item item : new Item[]{ModItems.WHITE_FLAG.get(), ModItems.BLACK_FLAG.get(), ModItems.BLUE_FLAG.get(), ModItems.BROWN_FLAG.get(), ModItems.CYAN_FLAG.get(), ModItems.GRAY_FLAG.get(), ModItems.GREEN_FLAG.get(), ModItems.LIGHT_BLUE_FLAG.get(), ModItems.LIGHT_GRAY_FLAG.get(), ModItems.LIME_FLAG.get(), ModItems.MAGENTA_FLAG.get(), ModItems.ORANGE_FLAG.get(), ModItems.PINK_FLAG.get(), ModItems.PURPLE_FLAG.get(), ModItems.RED_FLAG.get(), ModItems.YELLOW_FLAG.get()}) {
            register.accept(new ModResourceLocation("block/flag/" + Registry.ITEM.getKey(item).getPath()));
        }
        for (ResourceLocation id : new ResourceLocation[]{SlidingDoorBlockEntityRenderer.IRON_SLIDING_DOOR_MODEL, SlidingDoorBlockEntityRenderer.STEEL_SLIDING_DOOR_MODEL, SlidingDoorBlockEntityRenderer.DESH_SLIDING_DOOR_MODEL, SlidingDoorBlockEntityRenderer.OSTRUM_SLIDING_DOOR_MODEL, SlidingDoorBlockEntityRenderer.CALORITE_SLIDING_DOOR_MODEL, SlidingDoorBlockEntityRenderer.AIRLOCK_MODEL, SlidingDoorBlockEntityRenderer.REINFORCED_DOOR_MODEL, SlidingDoorBlockEntityRenderer.IRON_SLIDING_DOOR_MODEL_FLIPPED, SlidingDoorBlockEntityRenderer.STEEL_SLIDING_DOOR_MODEL_FLIPPED, SlidingDoorBlockEntityRenderer.DESH_SLIDING_DOOR_MODEL_FLIPPED, SlidingDoorBlockEntityRenderer.OSTRUM_SLIDING_DOOR_MODEL_FLIPPED, SlidingDoorBlockEntityRenderer.CALORITE_SLIDING_MODEL_FLIPPED, SlidingDoorBlockEntityRenderer.AIRLOCK_MODEL_FLIPPED, SlidingDoorBlockEntityRenderer.REINFORCED_DOOR_MODEL_FLIPPED}) {
            register.accept(id);
        }
        register.accept(LaunchPadBlockEntityRenderer.LAUNCH_PAD_MODEL);
    }

    public static void renderBlock(ResourceLocation texture, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {

        Minecraft client = Minecraft.getInstance();
        ModelManager manager = client.getModelManager();
        BakedModel model = ClientUtils.getModel(manager, texture);

        VertexConsumer vertexConsumer1 = vertexConsumers.getBuffer(RenderType.entityCutout(InventoryMenu.BLOCK_ATLAS));
        List<BakedQuad> quads1 = model.getQuads(null, null, client.level.random);
        PoseStack.Pose entry1 = matrices.last();

        for (BakedQuad quad : quads1) {
            vertexConsumer1.putBulkData(entry1, quad, 1, 1, 1, light, overlay);
        }
    }
}