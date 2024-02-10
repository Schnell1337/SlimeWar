package ru.schnell.slimewar.configuration;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import ru.schnell.util.RandomCollection;

public class EnchantConfig extends Config{

    public EnchantConfig(Plugin plugin) {
        super(plugin, "EnchantConfig");
    }

    @Override
    protected void onFirstLoad() {

    }

    @Override
    protected void checkDefault() {
        for (Enchantment enchantment : Enchantment.values()) {
            setIfNotExists(enchantment.getName() + ".chance", 100.0 / Enchantment.values().length);
        }
    }

    public RandomCollection<Enchantment> getEnchantments() {
        RandomCollection<Enchantment> enchantments = new RandomCollection<>();

        for (Enchantment enchantment : Enchantment.values()) {
            double chance = config.getDouble(enchantment.getName() + ".chance");
            enchantments.add(chance, enchantment);
        }

        return enchantments;
    }

}
