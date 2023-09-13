package earth.terrarium.adastra.common.blockentities.machines;

import earth.terrarium.adastra.common.blockentities.base.RecipeMachineBlockEntity;
import earth.terrarium.adastra.common.blockentities.base.sideconfig.Configuration;
import earth.terrarium.adastra.common.blockentities.base.sideconfig.ConfigurationEntry;
import earth.terrarium.adastra.common.blockentities.base.sideconfig.ConfigurationType;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.container.BiFluidContainer;
import earth.terrarium.adastra.common.menus.machines.SeparatorMenu;
import earth.terrarium.adastra.common.recipes.machines.SeparatingRecipe;
import earth.terrarium.adastra.common.registry.ModRecipeTypes;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.adastra.common.utils.TransferUtils;
import earth.terrarium.botarium.common.energy.impl.InsertOnlyEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedBlockEnergyContainer;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Predicate;

public class SeparatorBlockEntity extends RecipeMachineBlockEntity<SeparatingRecipe> implements BotariumFluidBlock<WrappedBlockFluidContainer>, GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final long[] lastFluid = new long[3];
    private final long[] fluidDifference = new long[3];
    private WrappedBlockFluidContainer fluidContainer;

    public SeparatorBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, 7);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // TODO
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new SeparatorMenu(id, inventory, this);
    }

    @Override
    public boolean shouldSync() {
        return getEnergyStorage().getStoredEnergy() > 0 && !getFluidContainer().getFluids().get(0).isEmpty();
    }

    @Override
    public WrappedBlockEnergyContainer getEnergyStorage() {
        if (this.energyContainer != null) return this.energyContainer;
        return this.energyContainer = new WrappedBlockEnergyContainer(
            this,
            new InsertOnlyEnergyContainer(100_000) {
                @Override
                public long maxInsert() {
                    return 2_500;
                }
            });
    }

    @Override
    public WrappedBlockFluidContainer getFluidContainer() {
        if (this.fluidContainer != null) return this.fluidContainer;
        return this.fluidContainer = new WrappedBlockFluidContainer(
            this,
            new BiFluidContainer(
                FluidHooks.buckets(10.0f),
                1,
                2,
                (tank, holder) -> level().getRecipeManager().getAllRecipesFor(ModRecipeTypes.SEPARATING.get())
                    .stream()
                    .anyMatch(r -> r.ingredient().matches(holder)),
                (tank, holder) -> level().getRecipeManager().getAllRecipesFor(ModRecipeTypes.SEPARATING.get())
                    .stream()
                    .anyMatch(r -> tank == 0 ?
                        r.resultFluid1().matches(holder) :
                        r.resultFluid2().matches(holder))));
    }

    @Override
    public void serverTick(ServerLevel level, long time, BlockState state, BlockPos pos) {
        if (canFunction()) tickSideInteractions(pos, d -> true);
    }

    @Override
    public void tickSideInteractions(BlockPos pos, Predicate<Direction> filter) {
        TransferUtils.pullItemsNearby(this, pos, new int[]{1}, this.getSideConfig().get(0), filter);
        TransferUtils.pullItemsNearby(this, pos, new int[]{3, 5}, this.getSideConfig().get(1), filter);
        TransferUtils.pushItemsNearby(this, pos, new int[]{2, 4, 6}, this.getSideConfig().get(2), filter);
        TransferUtils.pullEnergyNearby(this, pos, this.getEnergyStorage().maxInsert(), this.getSideConfig().get(3), filter);
        TransferUtils.pullFluidNearby(this, pos, this.getFluidContainer(), FluidHooks.buckets(0.2f), 0, this.getSideConfig().get(4), filter);
        TransferUtils.pushFluidNearby(this, pos, this.getFluidContainer(), FluidHooks.buckets(0.2f), 1, this.getSideConfig().get(5), filter);
        TransferUtils.pushFluidNearby(this, pos, this.getFluidContainer(), FluidHooks.buckets(0.2f), 2, this.getSideConfig().get(6), filter);
    }

    @Override
    public void recipeTick(ServerLevel level, WrappedBlockEnergyContainer energyStorage) {
        if (recipe == null) return;
        var fluidContainer = getFluidContainer();

        if (fluidContainer.getFluids().get(1).getFluidAmount() >= fluidContainer.getTankCapacity(1)) return;
        if (fluidContainer.getFluids().get(2).getFluidAmount() >= fluidContainer.getTankCapacity(2)) return;
        if (energyStorage.internalExtract(recipe.energy(), true) < recipe.energy()) return;
        if (fluidContainer.internalExtract(recipe.ingredient(), true).getFluidAmount() < recipe.ingredient().getFluidAmount())
            return;

        energyStorage.internalExtract(recipe.energy(), false);

        cookTime++;
        if (cookTime < cookTimeTotal) return;
        craft();
    }

    @Override
    public void craft() {
        if (recipe == null) return;

        fluidContainer.internalExtract(recipe.ingredient(), false);
        fluidContainer.internalInsert(recipe.resultFluid1(), false);
        fluidContainer.internalInsert(recipe.resultFluid2(), false);

        this.updateSlots();

        cookTime = 0;
        if (fluidContainer.getFluids().get(0).isEmpty()) {
            recipe = null;
        }
    }

    @Override
    public void update() {
        if (level().isClientSide()) return;
        level().getRecipeManager().getAllRecipesFor(ModRecipeTypes.SEPARATING.get())
            .stream()
            .filter(r -> r.ingredient().matches(getFluidContainer().getFluids().get(0)))
            .findFirst()
            .ifPresent(r -> {
                recipe = r;
                cookTimeTotal = r.cookingTime();
            });
        this.updateSlots();
    }

    @Override
    public void updateSlots() {
        FluidUtils.insertSlotToTank(this, 1, 2, 0);
        FluidUtils.extractTankToSlot(this, 3, 4, 1);
        FluidUtils.extractTankToSlot(this, 5, 6, 2);
    }

    @Override
    public void clientTick(ClientLevel level, long time, BlockState state, BlockPos pos) {
        super.clientTick(level, time, state, pos);
        if (time % 2 == 0) return;
        for (int i = 0; i < 3; i++) {
            this.fluidDifference[i] = this.getFluidContainer().getFluids().get(i).getFluidAmount() - this.lastFluid[i];
            this.lastFluid[i] = this.getFluidContainer().getFluids().get(i).getFluidAmount();
        }
    }

    public long fluidDifference(int tank) {
        return this.fluidDifference[tank];
    }

    @Override
    public List<ConfigurationEntry> getDefaultConfig() {
        return List.of(
            new ConfigurationEntry(ConfigurationType.SLOT, Configuration.NONE, ConstantComponents.SIDE_CONFIG_INPUT_SLOTS),
            new ConfigurationEntry(ConfigurationType.SLOT, Configuration.NONE, ConstantComponents.SIDE_CONFIG_EXTRACTION_SLOTS),
            new ConfigurationEntry(ConfigurationType.SLOT, Configuration.NONE, ConstantComponents.SIDE_CONFIG_OUTPUT_SLOTS),
            new ConfigurationEntry(ConfigurationType.ENERGY, Configuration.NONE, ConstantComponents.SIDE_CONFIG_ENERGY),
            new ConfigurationEntry(ConfigurationType.FLUID, Configuration.NONE, ConstantComponents.SIDE_CONFIG_INPUT_FLUID),
            new ConfigurationEntry(ConfigurationType.FLUID, Configuration.NONE, ConstantComponents.SIDE_CONFIG_OUTPUT_FLUID),
            new ConfigurationEntry(ConfigurationType.FLUID, Configuration.NONE, ConstantComponents.SIDE_CONFIG_OUTPUT_FLUID)
        );
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return new int[]{1, 2, 3, 4, 5, 6};
    }
}