package mods.shiborui.fermentation;

import mods.shiborui.fermentation.block.DryingGrainCrop;
import mods.shiborui.fermentation.block.GerminatingGrainCrop;
import mods.shiborui.fermentation.block.HopsVine;
import mods.shiborui.fermentation.block.Kettle;
import mods.shiborui.fermentation.block.Tank;
import mods.shiborui.fermentation.block.VineScaffold;
import mods.shiborui.fermentation.block.WaterproofBarrel;
import mods.shiborui.fermentation.block.YeastBin;
import mods.shiborui.fermentation.item.Beer;
import mods.shiborui.fermentation.item.BucketBeer;
import mods.shiborui.fermentation.item.BucketHoppedWort;
import mods.shiborui.fermentation.item.BucketRuinedBrew;
import mods.shiborui.fermentation.item.BucketSweetWort;
import mods.shiborui.fermentation.item.DriedGrain;
import mods.shiborui.fermentation.item.GerminatedGrain;
import mods.shiborui.fermentation.item.Grain;
import mods.shiborui.fermentation.item.Hops;
import mods.shiborui.fermentation.item.HopsSeeds;
import mods.shiborui.fermentation.item.HydratedGrain;
import mods.shiborui.fermentation.item.MaltedGrain;
import mods.shiborui.fermentation.item.MilledGrain;
import mods.shiborui.fermentation.item.Mug;
import mods.shiborui.fermentation.item.QuernStone;
import mods.shiborui.fermentation.item.VineAssembly;
import mods.shiborui.fermentation.item.Yeast;
import mods.shiborui.fermentation.liquid.LiquidBeer;
import mods.shiborui.fermentation.liquid.LiquidHoppedWort;
import mods.shiborui.fermentation.liquid.LiquidSweetWort;
import mods.shiborui.fermentation.tileentity.TileEntityKettle;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import mods.shiborui.fermentation.tileentity.TileEntityWaterproofBarrel;
import mods.shiborui.fermentation.tileentity.TileEntityYeastBin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.FMLCommonHandler;
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
import cpw.mods.fml.relauncher.Side;


@Mod(modid="Fermentation", name="Fermentation", version="0.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels={"FmtnTank", "FmtnKettle", "FmtnWPBarrel"}, packetHandler = PacketHandler.class)
public class Fermentation {

	public static Item mug;
	public static ItemSeeds grain;
	public static Item driedGrain;
	public static ItemSeeds hydratedGrain;
	public static Item germinatedGrain;
	public static Item maltedGrain;
	public static Item milledGrain;
	public static Item quernStone;
	public static Item bucketSweetWort;
	public static Item bucketHoppedWort;
	public static Item bucketBeer;
	public static Item bucketRuinedBrew;
	public static Item yeast;
	public static Item hops;
	public static Item vineAssembly;
	public static Item hopsSeeds;
	public static Item beer;
	public static Item liquidSweetWort;
	public static Item liquidHoppedWort;
	public static Item liquidBeer;
	public static Block waterproofBarrel;
	public static Block tank;
	public static Block kettle;
	public static Block yeastBin;
	public static Block dryingGrainCrop;
	public static Block germinatingGrainCrop;
	public static Block vineScaffold;
	public static Block hopsVine;
	
	public static int mugID = 5000;
	public static int grainID = 5001;
	public static int driedGrainID = 5002;
	public static int hydratedGrainID = 5003;
	public static int germinatedGrainID = 5004;
	public static int maltedGrainID = 5005;
	public static int milledGrainID = 5006;
	public static int quernStoneID = 5007;
	public static int bucketSweetWortID = 5008;
	public static int bucketHoppedWortID = 5009;
	public static int bucketBeerID = 5010;
	public static int bucketRuinedBrewID = 5011;
	public static int yeastID = 5012;
	public static int hopsID = 5013;
	public static int vineAssemblyID = 5014;
	public static int hopsSeedsID = 5015;
	public static int beerID = 5016;
	public static int liquidSweetWortID = 5017;
	public static int liquidHoppedWortID = 5018;
	public static int liquidBeerID = 5019;
	public static int waterproofBarrelID = 500;
	public static int tankID = 501;
	public static int kettleID = 502;
	public static int yeastBinID = 503;
	public static int dryingGrainCropID = 504;
	public static int germinatingGrainCropID = 505;
	public static int vineScaffoldID = 506;
	public static int hopsVineID = 507;
	
	
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
            
            vineScaffold = new VineScaffold(vineScaffoldID);
            GameRegistry.registerBlock(vineScaffold, "fermentationVineScaffold");
            LanguageRegistry.addName(vineScaffold, "Vine Scaffold");
            
            hopsVine = new HopsVine(hopsVineID);
            GameRegistry.registerBlock(hopsVine, "fermentationHopsVine");
            LanguageRegistry.addName(hopsVine, "Hops Vine");
            
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
            
            bucketSweetWort = new BucketSweetWort(bucketSweetWortID);
            bucketSweetWort.setContainerItem(Item.bucketEmpty);
            LanguageRegistry.addName(bucketSweetWort, "Sweet Wort");
            
            bucketHoppedWort = new BucketHoppedWort(bucketHoppedWortID);
            bucketHoppedWort.setContainerItem(Item.bucketEmpty);
            LanguageRegistry.addName(bucketHoppedWort, "Hopped Wort");
            
            bucketBeer = new BucketBeer(bucketBeerID);
            bucketBeer.setContainerItem(Item.bucketEmpty);
            
            beer = new Beer(beerID);
            
            liquidBeer = new LiquidBeer(liquidBeerID);
            
            String beerName = "";
            for(int subItem = 0; subItem < 64; subItem++) {
            	beerName = LiquidBeer.getNameFromDamage(subItem);
            	
            	LanguageRegistry.addName(new ItemStack(bucketBeer, 1, subItem), beerName);
            	LanguageRegistry.addName(new ItemStack(beer, 1, subItem), beerName);
                LanguageRegistry.addName(new ItemStack(liquidBeer, 1, subItem), beerName);
                
                LiquidDictionary.getOrCreateLiquid(beerName, new LiquidStack(liquidBeer.itemID, 1, subItem));
                
                LiquidContainerData containerData = new LiquidContainerData(new LiquidStack(liquidBeer.itemID, LiquidContainerRegistry.BUCKET_VOLUME, subItem), 
                		new ItemStack(Fermentation.bucketBeer, 1, subItem), new ItemStack(Item.bucketEmpty));
                LiquidContainerRegistry.registerLiquid(containerData);
                
                containerData = new LiquidContainerData(new LiquidStack(liquidBeer.itemID, LiquidContainerRegistry.BUCKET_VOLUME/8, subItem), 
                		new ItemStack(Fermentation.beer, 1, subItem), new ItemStack(Fermentation.mug));
                LiquidContainerRegistry.registerLiquid(containerData);
            }
            
            bucketRuinedBrew = new BucketRuinedBrew(bucketRuinedBrewID);
            bucketRuinedBrew.setContainerItem(Item.bucketEmpty);
            LanguageRegistry.addName(bucketRuinedBrew, "Ruined Brew");
            
            yeast = new Yeast(yeastID);
            MinecraftForge.addGrassSeed(new ItemStack(yeast), 1);
            LanguageRegistry.addName(yeast, "Yeast");
            
            hops = new Hops(hopsID);
            LanguageRegistry.addName(hops, "Hops");
            
            vineAssembly = new VineAssembly(vineAssemblyID, hopsVineID, 2);
            LanguageRegistry.addName(new ItemStack(vineAssembly, 1, 0), "Vine Assembly");
            LanguageRegistry.addName(new ItemStack(vineAssembly, 1, 1), "Hops Vine Assembly");
            
            hopsSeeds = new HopsSeeds(hopsSeedsID);
            MinecraftForge.addGrassSeed(new ItemStack(hopsSeeds), 1);
            LanguageRegistry.addName(hopsSeeds, "Hops Seeds");

            liquidSweetWort = new LiquidSweetWort(liquidSweetWortID);
            LanguageRegistry.addName(liquidSweetWort, "Sweet Wort");
            
            String wortName = "";
            for(int subItem = 0; subItem < 4; subItem++) {
            	wortName = LiquidSweetWort.getNameFromDamage(subItem);
            	
            	LanguageRegistry.addName(new ItemStack(bucketSweetWort, 1, subItem), wortName);
            	LanguageRegistry.addName(new ItemStack(liquidSweetWort, 1, subItem), wortName);
            	
            	LiquidDictionary.getOrCreateLiquid(wortName, new LiquidStack(liquidSweetWort.itemID, 1, subItem));
                
                LiquidContainerData containerData = new LiquidContainerData(new LiquidStack(liquidSweetWort.itemID, LiquidContainerRegistry.BUCKET_VOLUME, subItem), 
                		new ItemStack(Fermentation.bucketSweetWort, 1, subItem), new ItemStack(Item.bucketEmpty));
                LiquidContainerRegistry.registerLiquid(containerData);
            }
            
            liquidHoppedWort = new LiquidHoppedWort(liquidHoppedWortID);
            LanguageRegistry.addName(liquidHoppedWort, "Hopped Wort");
            
            for(int subItem = 0; subItem < 16; subItem++) {
            	wortName = LiquidHoppedWort.getNameFromDamage(subItem);
            	
            	LanguageRegistry.addName(new ItemStack(bucketHoppedWort, 1, subItem), wortName);
            	LanguageRegistry.addName(new ItemStack(liquidHoppedWort, 1, subItem), wortName);
            	
            	LiquidDictionary.getOrCreateLiquid(wortName, new LiquidStack(liquidHoppedWort.itemID, 1, subItem));
                
                LiquidContainerData containerData = new LiquidContainerData(new LiquidStack(liquidHoppedWort.itemID, LiquidContainerRegistry.BUCKET_VOLUME, subItem), 
                		new ItemStack(Fermentation.bucketHoppedWort, 1, subItem), new ItemStack(Item.bucketEmpty));
                LiquidContainerRegistry.registerLiquid(containerData);
            }
            
            waterproofBarrel = new WaterproofBarrel(waterproofBarrelID, Material.wood);
            GameRegistry.registerBlock(waterproofBarrel, "fermentationWaterproofBarrel");
            LanguageRegistry.addName(waterproofBarrel, "Waterproof Barrel");
            
            tank = new Tank(tankID, Material.iron);
            GameRegistry.registerBlock(tank, "fermentationTank");
            LanguageRegistry.addName(tank, "Tank");
            
            yeastBin = new YeastBin(yeastBinID, Material.wood);
            GameRegistry.registerBlock(yeastBin, "fermentationYeastBin");
            LanguageRegistry.addName(yeastBin, "Yeast Bin");
            
            kettle = new Kettle(kettleID, Material.iron);
            GameRegistry.registerBlock(kettle, "fermentationKettle");
            LanguageRegistry.addName(kettle,  "Kettle");
            
            GameRegistry.registerTileEntity(TileEntityTank.class, "containerTank");
            GameRegistry.registerTileEntity(TileEntityYeastBin.class, "containerYeastBin");
            GameRegistry.registerTileEntity(TileEntityKettle.class, "containerKettle");
            GameRegistry.registerTileEntity(TileEntityWaterproofBarrel.class, "containerWaterproofBarrel");
        }
        
        private void registerRecipes() {
        	ItemStack stoneStack = new ItemStack(Block.stone);
        	ItemStack glassStack = new ItemStack(Block.glass);
        	ItemStack woodStack = new ItemStack(Block.wood);
        	ItemStack planksStack = new ItemStack(Block.planks);
        	ItemStack slabStack = new ItemStack(Block.woodSingleSlab);
        	ItemStack stickStack = new ItemStack(Item.stick);
        	ItemStack wheatStack = new ItemStack(Item.wheat);
        	ItemStack cauldronStack = new ItemStack(Block.cauldron);
        	ItemStack blockIronStack = new ItemStack(Block.blockIron);
        	ItemStack ingotIronStack = new ItemStack(Item.ingotIron);
        	ItemStack ladderStack = new ItemStack(Block.ladder);
        	ItemStack furnaceStack = new ItemStack(Block.furnaceIdle);
        	
        	ItemStack maltedGrainStack = new ItemStack(maltedGrain);
        	ItemStack quernStoneStack = new ItemStack(quernStone);
        	ItemStack hopsStack = new ItemStack(hops);
        	ItemStack hopsSeedsStack = new ItemStack(hopsSeeds);
        	
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
        	
        	GameRegistry.addRecipe(new ItemStack(yeastBin), 
        			"x x", "x x", "xxx", 
        			'x', planksStack);
        	
        	GameRegistry.addRecipe(new ItemStack(kettle),
        			"xxx", "x x", "xyx",
        			'x', ingotIronStack, 'y', furnaceStack);
        	
        	GameRegistry.addRecipe(new ItemStack(quernStone), 
        			"x  ", "yx ", "y  ",
        			'x', stickStack, 'y', stoneStack);
        	
        	GameRegistry.addRecipe(new ItemStack(quernStone), 
        			" x ", " yx", " y ",
        			'x', stickStack, 'y', stoneStack);
        	
        	GameRegistry.addShapelessRecipe(new ItemStack(milledGrain), 
        			quernStoneStack, maltedGrainStack);
        	
        	GameRegistry.addShapelessRecipe(new ItemStack(hopsSeeds), 
        			hopsStack);
        	
        	GameRegistry.addShapelessRecipe(new ItemStack(vineAssembly, 1, 1), 
        			hopsSeedsStack, ladderStack, ladderStack);
        	
        	GameRegistry.addSmelting(germinatedGrain.itemID, new ItemStack(maltedGrain), 0);
        }
}