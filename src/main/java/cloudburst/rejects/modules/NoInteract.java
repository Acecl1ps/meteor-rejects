package cloudburst.rejects.modules;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import cloudburst.rejects.MeteorRejectsAddon;

import java.util.ArrayList;
import java.util.List;

public class NoInteract extends Module {
    
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
            .name("blocks")
            .description("Blocks interactions with certain blocks")
            .defaultValue(getDefaultBlocks())
            .filter(this::filterBlocks)
            .build()
    );
    
    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("entities")
            .description("Entities to block interaction with")
            .defaultValue(new Object2BooleanOpenHashMap<>(0))
            .build()
    );
    
    public NoInteract() {
        super(MeteorRejectsAddon.CATEGORY, "no-interact", "Blocks interactions with certain types of inputs.");
    }
    @EventHandler
    private void onScreenOpen(OpenScreenEvent event) {
        if (event.screen == null) return;
        if (!event.screen.isPauseScreen() && !(event.screen instanceof AbstractInventoryScreen) && (event.screen instanceof HandledScreen)) event.setCancelled(true);
    }
    
    public boolean noInteractBlock(Block block) {
        return Modules.get().get(this.getClass()).isActive() && blocks.get().contains(block);
    }
    
    public boolean noInteractEntity(Entity entity) {
        return Modules.get().get(this.getClass()).isActive() && entities.get().getBoolean(entity.getType());
    }
    
    private List<Block> getDefaultBlocks() {
        
        ArrayList<Block> defaultBlocks = new ArrayList<>();
        for (Block block : Registry.BLOCK) {
            if (filterBlocks(block)) defaultBlocks.add(block);
        }
        return defaultBlocks;
    }
    
    private boolean filterBlocks(Block block) {
        return isStorageBlock(block) || isRedstoneBlock(block) ||
                
                // Others
                block instanceof RespawnAnchorBlock ||
                block instanceof BedBlock;
    }
    
    private boolean isStorageBlock(Block block) {
        return block instanceof ChestBlock ||
                block instanceof EnderChestBlock ||
                block instanceof AbstractFurnaceBlock ||
                block instanceof BrewingStandBlock ||
                block instanceof BarrelBlock ||
                block instanceof HopperBlock ||
                block instanceof ShulkerBoxBlock ||
                block instanceof DispenserBlock;
    }
    
    private boolean isRedstoneBlock(Block block) {
        return block instanceof TrapdoorBlock ||
                block instanceof DoorBlock ||
                block instanceof FenceGateBlock ||
                block instanceof LeverBlock ||
                block instanceof AbstractButtonBlock ||
                block instanceof AbstractPressurePlateBlock ||
                block instanceof RepeaterBlock ||
                block instanceof ComparatorBlock;
    }
}