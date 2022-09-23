package com.github.alexnijjar.ad_astra.registry;

import com.github.alexnijjar.ad_astra.recipes.CompressingRecipe;
import com.github.alexnijjar.ad_astra.recipes.CryoFuelConversionRecipe;
import com.github.alexnijjar.ad_astra.recipes.FluidConversionRecipe;
import com.github.alexnijjar.ad_astra.recipes.GeneratingRecipe;
import com.github.alexnijjar.ad_astra.recipes.HammerShapelessRecipe;
import com.github.alexnijjar.ad_astra.recipes.ModRecipeType;
import com.github.alexnijjar.ad_astra.recipes.NasaWorkbenchRecipe;
import com.github.alexnijjar.ad_astra.recipes.OxygenConversionRecipe;
import com.github.alexnijjar.ad_astra.recipes.SpaceStationRecipe;
import com.github.alexnijjar.ad_astra.util.ModIdentifier;

import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.registry.Registry;

public class ModRecipes {

	public static ModRecipeType<GeneratingRecipe> GENERATING_RECIPE;
	public static ModRecipeType<CompressingRecipe> COMPRESSING_RECIPE;
	public static ModRecipeType<SpaceStationRecipe> SPACE_STATION_RECIPE;
	public static ModRecipeType<NasaWorkbenchRecipe> NASA_WORKBENCH_RECIPE;
	public static ModRecipeType<FluidConversionRecipe> FUEL_CONVERSION_RECIPE;
	public static ModRecipeType<OxygenConversionRecipe> OXYGEN_CONVERSION_RECIPE;
	public static ModRecipeType<CryoFuelConversionRecipe> CRYO_FUEL_CONVERSION_RECIPE;

	public static RecipeSerializer<ShapelessRecipe> HAMMER_SERIALIZER;
	public static RecipeSerializer<GeneratingRecipe> GENERATING_SERIALIZER;
	public static RecipeSerializer<CompressingRecipe> COMPRESSING_SERIALIZER;
	public static RecipeSerializer<SpaceStationRecipe> SPACE_STATION_SERIALIZER;
	public static RecipeSerializer<NasaWorkbenchRecipe> NASA_WORKBENCH_SERIALIZER;
	public static RecipeSerializer<FluidConversionRecipe> FUEL_CONVERSION_SERIALIZER;
	public static RecipeSerializer<OxygenConversionRecipe> OXYGEN_CONVERSION_SERIALIZER;
	public static RecipeSerializer<CryoFuelConversionRecipe> CRYO_FUEL_CONVERSION_SERIALIZER;

	public static void register() {

		// Recipe Types.
		GENERATING_RECIPE = register(new ModRecipeType<>(new ModIdentifier("generating")));
		COMPRESSING_RECIPE = register(new ModRecipeType<>(new ModIdentifier("compressing")));
		SPACE_STATION_RECIPE = register(new ModRecipeType<>(new ModIdentifier("space_station")));
		NASA_WORKBENCH_RECIPE = register(new ModRecipeType<>(new ModIdentifier("nasa_workbench")));
		FUEL_CONVERSION_RECIPE = register(new ModRecipeType<>(new ModIdentifier("fuel_conversion")));
		OXYGEN_CONVERSION_RECIPE = register(new ModRecipeType<>(new ModIdentifier("oxygen_conversion")));
		CRYO_FUEL_CONVERSION_RECIPE = register(new ModRecipeType<>(new ModIdentifier("cryo_fuel_conversion")));

		// Recipe Serializers.
		HAMMER_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("hammering"), new HammerShapelessRecipe.Serializer());
		GENERATING_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("generating"), new CodecRecipeSerializer<>(GENERATING_RECIPE, GeneratingRecipe::codec));
		COMPRESSING_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("compressing"), new CodecRecipeSerializer<>(COMPRESSING_RECIPE, CompressingRecipe::codec));
		SPACE_STATION_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("space_station"), new CodecRecipeSerializer<>(SPACE_STATION_RECIPE, SpaceStationRecipe::codec));
		NASA_WORKBENCH_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("nasa_workbench"), new CodecRecipeSerializer<>(NASA_WORKBENCH_RECIPE, NasaWorkbenchRecipe::codec));
		FUEL_CONVERSION_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("fuel_conversion"), new CodecRecipeSerializer<>(FUEL_CONVERSION_RECIPE, FluidConversionRecipe::codec));
		OXYGEN_CONVERSION_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("oxygen_conversion"), new CodecRecipeSerializer<>(OXYGEN_CONVERSION_RECIPE, OxygenConversionRecipe::oxygenCodec));
		CRYO_FUEL_CONVERSION_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ModIdentifier("cryo_fuel_conversion"), new CodecRecipeSerializer<>(CRYO_FUEL_CONVERSION_RECIPE, CryoFuelConversionRecipe::codec));
	}

	private static <T extends ModRecipeType<?>> T register(T recipe) {
		Registry.register(Registry.RECIPE_TYPE, recipe.getId(), recipe);
		return recipe;
	}
}