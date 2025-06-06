package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EntityEquipmentLootItem extends ItemLootItem {

    private final NumberProvider mainHandDropChance;
    private final NumberProvider offHandDropChance;
    private final NumberProvider helmetDropChance;
    private final NumberProvider chestplateDropChance;
    private final NumberProvider leggingsDropChance;
    private final NumberProvider bootsDropChance;
    private final boolean dropInventory;

    public EntityEquipmentLootItem(NumberProvider mainHandDropChance, NumberProvider offHandDropChance, NumberProvider helmetDropChance, NumberProvider chestplateDropChance, NumberProvider leggingsDropChance, NumberProvider bootsDropChance, boolean dropInventory) {
        super();
        this.mainHandDropChance = mainHandDropChance;
        this.offHandDropChance = offHandDropChance;
        this.helmetDropChance = helmetDropChance;
        this.chestplateDropChance = chestplateDropChance;
        this.leggingsDropChance = leggingsDropChance;
        this.bootsDropChance = bootsDropChance;
        this.dropInventory = dropInventory;
    }

    @Override
    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> droppedEquipment = new ArrayList<>();
        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (lootedEntity.isEmpty())
            return droppedEquipment;

        LivingEntity entity = lootedEntity.get();

        if (this.dropInventory && entity instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) entity).getInventory();
            droppedEquipment.addAll(List.of(inventory.getStorageContents()));
        }

        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null)
            return droppedEquipment;

        double mainHandChance = this.mainHandDropChance != null ? this.mainHandDropChance.getDouble(context) : equipment.getItemInMainHandDropChance();
        double offHandChance = this.offHandDropChance != null ? this.offHandDropChance.getDouble(context) : equipment.getItemInOffHandDropChance();
        double helmetChance = this.helmetDropChance != null ? this.helmetDropChance.getDouble(context) : equipment.getHelmetDropChance();
        double chestplateChance = this.chestplateDropChance != null ? this.chestplateDropChance.getDouble(context) : equipment.getChestplateDropChance();
        double leggingsChance = this.leggingsDropChance != null ? this.leggingsDropChance.getDouble(context) : equipment.getLeggingsDropChance();
        double bootsChance = this.bootsDropChance != null ? this.bootsDropChance.getDouble(context) : equipment.getBootsDropChance();

        if (equipment.getItemInMainHand().getType() != Material.AIR && LootUtils.checkChance(mainHandChance))
            droppedEquipment.add(equipment.getItemInMainHand());

        if (equipment.getItemInOffHand().getType() != Material.AIR && LootUtils.checkChance(offHandChance))
            droppedEquipment.add(equipment.getItemInOffHand());

        if (equipment.getHelmet() != null && LootUtils.checkChance(helmetChance))
            droppedEquipment.add(equipment.getHelmet());

        if (equipment.getChestplate() != null && LootUtils.checkChance(chestplateChance))
            droppedEquipment.add(equipment.getChestplate());

        if (equipment.getLeggings() != null && LootUtils.checkChance(leggingsChance))
            droppedEquipment.add(equipment.getLeggings());

        if (equipment.getBoots() != null && LootUtils.checkChance(bootsChance))
            droppedEquipment.add(equipment.getBoots());

        return droppedEquipment;
    }

    public static EntityEquipmentLootItem fromSection(ConfigurationSection section) {
        NumberProvider mainHandDropChance = NumberProvider.fromSection(section, "main-hand-drop-chance", null);
        NumberProvider offHandDropChance = NumberProvider.fromSection(section, "off-hand-drop-chance", null);
        NumberProvider helmetDropChance = NumberProvider.fromSection(section, "helmet-drop-chance", null);
        NumberProvider chestplateDropChance = NumberProvider.fromSection(section, "chestplate-drop-chance", null);
        NumberProvider leggingsDropChance = NumberProvider.fromSection(section, "leggings-drop-chance", null);
        NumberProvider bootsDropChance = NumberProvider.fromSection(section, "boots-drop-chance", null);
        boolean dropInventory = section.getBoolean("drop-inventory", false);
        return new EntityEquipmentLootItem(mainHandDropChance, offHandDropChance, helmetDropChance, chestplateDropChance, leggingsDropChance, bootsDropChance, dropInventory);
    }

}
