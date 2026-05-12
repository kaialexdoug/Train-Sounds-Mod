package ashenlight.train_sounds;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.*;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.sun.jna.platform.win32.WinBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Train_sounds.MODID)
public class Train_sounds {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "train_sounds";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "train_sounds" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "train_sounds" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "train_sounds" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "train_sounds:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "train_sounds:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "train_sounds:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));

    // Creates a creative tab with the id "train_sounds:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
    }).build());

    public Train_sounds() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SOUNDS.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> MOTOR_HUM = SOUNDS.register("motor_hum",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "motor_hum")));

    public static class EngineSoundInstance extends EntityBoundSoundInstance {

        private final CarriageContraptionEntity carriage;

        // TARGET is where we want the volume to be
        // this.volume creeps toward it each tick creating the fade effect
        private float targetVolume = 0.0f;
        private static final float MAX_VOLUME = 0.3f;  // adjust to taste
        private static final float FADE_SPEED = 0.02f; // higher = faster fade

        private double lastX;
        private double lastZ;
        private double actualMovement;

        private boolean manualStopped = false;

        private float currentPitch = 0.8f;
        private static final float PITCH_SMOOTH = 0.1f; // lower = smoother but slower;

        public EngineSoundInstance(CarriageContraptionEntity carriage, SoundEvent sound) {
            super(sound, SoundSource.BLOCKS, 1.0f, 1.0f, carriage, 0L);
            this.relative = false;
            this.carriage = carriage;
            this.looping = true;
            this.attenuation = Attenuation.LINEAR;
            this.volume = 0.01f; // small but non-zero so sound manager doesn't discard it

            this.lastX = carriage.getX();
            this.lastZ = carriage.getZ();
            this.actualMovement = 0;

        }

        @Override
        public void tick() {
            System.out.println("Sound position: " + this.x + " " + this.y + " " + this.z);
            System.out.println("Carriage position: " + carriage.getX() + " " + carriage.getY() + " " + carriage.getZ());

            if (!carriage.isAlive()) {
                this.stop();
                return;
            }

            // Get the current position this tick
            double currentX = carriage.getX();
            double currentZ = carriage.getZ();

            this.actualMovement = Math.sqrt(
                    Math.pow(currentX - lastX, 2) + Math.pow(currentZ - lastZ, 2)
            );

            lastX = currentX;
            lastZ = currentZ;

            if (this.actualMovement <= 0.001) {
                // Train didn't physically move this tick — fade out
                targetVolume = 0.0f;
                // Once fully silent, stop the instance cleanly
                if (this.volume <= 0.015f) {
                    this.stop();
                    this.manualStopped = true;
                    return;
                }
            } else {
                // Train moving — fade in
                targetVolume = MAX_VOLUME;

                this.x = currentX;
                this.y = carriage.getY();
                this.z = currentZ;

                float targetPitch = (float) Math.min(0.8f + actualMovement * 0.5f, 2.0f);
                currentPitch += (targetPitch - currentPitch) * PITCH_SMOOTH;
                this.pitch = currentPitch;
            }

            // Nudge volume toward target each tick — this creates the smooth fade
            if (this.volume < targetVolume) {
                this.volume = Math.min(this.volume + FADE_SPEED, targetVolume);
            } else if (this.volume > targetVolume) {
                this.volume = Math.max(this.volume - FADE_SPEED, targetVolume);
            }
        }

        public boolean isMoving() {
            return this.actualMovement > 0;
        }

        public boolean hasStopped() {
            return this.manualStopped;
        }
    }

        // This is the "Update" part of your mod
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class TrainSoundEventHandler {

        private static final Map<Integer, EngineSoundInstance> activeSounds = new HashMap<>();
        private static final Map<Integer, double[]> lastPositions = new HashMap<>();

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {

                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null || mc.player == null) return;

                mc.level.entitiesForRendering().forEach(entity -> {
                    if (entity instanceof CarriageContraptionEntity carriage) {

//                        Minecraft.getInstance().getSoundManager().stop(
//                                new ResourceLocation("create", "steam"), SoundSource.NEUTRAL
//                        );
                        int id = entity.getId();

                        double currentX = carriage.getX();
                        double currentZ = carriage.getZ();

                        double[] lastPos = lastPositions.getOrDefault(id, new double[]{currentX, currentZ});
                        double movement = Math.sqrt(
                                Math.pow(currentX - lastPos[0], 2) +
                                        Math.pow(currentZ - lastPos[1], 2)
                        );

                        lastPositions.put(id, new double[]{currentX, currentZ});

                        // Now use movement as the gate instead of train.speed
                        if (!activeSounds.containsKey(id) && movement > 0.001) {
                            EngineSoundInstance sound = new EngineSoundInstance(
                                    carriage, Train_sounds.MOTOR_HUM.get()
                            );
                            mc.getSoundManager().play(sound);
                            activeSounds.put(id, sound);
                        }

                        // Check if there's an existing sound for this carriage
                        EngineSoundInstance existingSound = activeSounds.get(id);
                        if (existingSound != null) {
                            if (existingSound.hasStopped() && !existingSound.isMoving()) {
                                activeSounds.remove(id);
                            }
                        }

                        if (!carriage.isAlive()) {
                            activeSounds.remove(id);
                        }
                    }
                });
            }
        }
    }
}
