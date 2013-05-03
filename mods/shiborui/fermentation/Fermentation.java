package mods.shiborui.fermentation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod(modid="Fermentation", name="Fermentation", version="0.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels={"FermentationTank"}, packetHandler = PacketHandler.class)
public class Fermentation {

	protected static Item mug;
	protected static ItemSeeds grain;
	protected static Item driedGrain;
	protected static ItemSeeds hydratedGrain;
	protected static Item germinatedGrain;
	protected static Item maltedGrain;
	protected static Item milledGrain;
	protected static Item quernStone;
	protected static Block waterproofBarrel;
	protected static Block tank;
	protected static Block dryingGrainCrop;
	protected static Block germinatingGrainCrop;
	
	private static int mugID = 5000;
	private static int grainID = 5001;
	private static int driedGrainID = 5002;
	private static int hydratedGrainID = 5003;
	private static int germinatedGrainID = 5004;
	private static int maltedGrainID = 5005;
	private static int milledGrainID = 5006;
	private static int quernStoneID = 5007;
	private static int waterproofBarrelID = 500;
	private static int tankID = 501;
	private static int dryingGrainCropID = 502;
	private static int germinatingGrainCropID = 503;
	
	
        // The instance of your mod that Forge uses.
        @Instance("Fermentation")
        public static Fermentation instance;
        
        // Says where the client and server 'proxy' code is loaded.
        @SidedProxy(clientSide="mods.shiborui.fermentation.client.ClientProxy", serverSide="mods.shiborui.fermentation.CommonProxy")
        public static CommonProxy proxy;
        
        @PreInit
        public void preInit(FMLPreInitializationEvent event) {
                // Stub Method
        }
        
        @Init
        public void load(FMLInitializationEvent event) {
                proxy.registerRenderers();
                NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
                
                registerItems();
                registerRecipes();
                
        }
        
        @PostInit
        public void postInit(FMLPostInitializationEvent event) {
                // Stub Method
        }
        
        private void registerItems() {
        	mug = new Mug(mugID);
            LanguageRegistry.addName(mug, "Mug");
            
            dryingGrainCrop = new DryingGrainCrop(dryingGrainCropID);
            GameRegistry.registerBlock(dryingGrainCrop, "fermentationDryingGrainCrop");
            
            germinatingGrainCrop = new GerminatingGrainCrop(germinatingGrainCropID);
            GameRegistry.registerBlock(germinatingGrainCrop, "fermentationGerminatingGrainCrop");
            
            grain = new Grain(grainID, dryingGrainCropID, 1);
            LanguageRegistry.addName(grain, "Grain");
            
            driedGrain = new DriedGrain(driedGrainID);
            LanguageRegistry.addName(driedGrain, "Dried Grain");
            
            hydratedGrain = new HydratedGrain(hydratedGrainID, germinatingGrainCropID, 1);
            LanguageRegistry.addName(hydratedGrain, "Hydrated Grain");
            
            germinatedGrain = new GerminatedGrain(germinatedGrainID);
            LanguageRegistry.addName(germinatedGrain, "Germinated Grain");
            
            maltedGrain = new MaltedGrain(maltedGrainID);
            LanguageRegistry.addName(maltedGrain, "Malted Grain");
            
            milledGrain = new MilledGrain(milledGrainID);
            LanguageRegistry.addName(milledGrain, "Milled Grain");
            
            quernStone = new QuernStone(quernStoneID);
            quernStone.setContainerItem(quernStone);
            LanguageRegistry.addName(quernStone, "Quern Stone");
            
            waterproofBarrel = new WaterproofBarrel(waterproofBarrelID, Material.wood);
            GameRegistry.registerBlock(waterproofBarrel, "fermentationWaterproofBarrel");
            LanguageRegistry.addName(waterproofBarrel, "Waterproof Barrel");
            
            tank = new Tank(tankID, Material.iron);
            GameRegistry.registerBlock(tank, "fermentationTank");
            LanguageRegistry.addName(tank, "Tank");
            
            GameRegistry.registerTileEntity(TileEntityTank.class, "containerTank");
        }
        
        private void registerRecipes() {
        	ItemStack stoneStack = new ItemStack(Block.stone);
        	ItemStack glassStack = new ItemStack(Block.glass);
        	ItemStack woodStack = new ItemStack(Block.wood);
        	ItemStack slabStack = new ItemStack(Block.woodSingleSlab);
        	ItemStack stickStack = new ItemStack(Item.stick);
        	ItemStack wheatStack = new ItemStack(Item.wheat);
        	ItemStack cauldronStack = new ItemStack(Block.cauldron);
        	ItemStack blockIronStack = new ItemStack(Block.blockIron);
        	ItemStack ingotIronStack = new ItemStack(Item.ingotIron);
        	
        	ItemStack maltedGrainStack = new ItemStack(maltedGrain);
        	ItemStack quernStoneStack = new ItemStack(quernStone);
        	
        	GameRegistry.addRecipe(new ItemStack(mug, 16), 
        			"xx ", "xxx", "xx ", 
        	        'x', glassStack);
        	
        	GameRegistry.addShapelessRecipe(new ItemStack(grain), 
        			wheatStack);
        	
        	GameRegistry.addRecipe(new ItemStack(waterproofBarrel), 
        			"xyx", "x x", "xyx", 
        			'x', woodStack, 'y', slabStack);
        	
        	GameRegistry.addRecipe(new ItemStack(tank), 
        			"xxx", "x x", "xyx", 
        			'x', ingotIronStack, 'y', blockIronStack);
        	
        	GameRegistry.addRecipe(new ItemStack(quernStone), 
        			"x  ", "yx ", "y  ",
        			'x', stickStack, 'y', stoneStack);
        	
        	GameRegistry.addRecipe(new ItemStack(quernStone), 
        			" x ", " yx", " y ",
        			'x', stickStack, 'y', stoneStack);
        	
        	GameRegistry.addShapelessRecipe(new ItemStack(milledGrain), 
        			quernStoneStack, maltedGrainStack);
        	
        	GameRegistry.addSmelting(germinatedGrain.itemID, new ItemStack(maltedGrain), 0);
        }
}