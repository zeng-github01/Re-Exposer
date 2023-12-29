package com.zengyj.exposer.util;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.relauncher.Side;


/*Used to handle Mod integration. Code comes from aeadditons or ExtraCells*/
public class Integration {
    public enum Mods{
        AE2("Applied Energistics 2","AE2 Unofficial Extended Life");



        Mods(String modid) {
            this(modid, modid);
        }

        Mods(String modid, String modName, Side side) {
            this.mod_id = modid;
            this.mod_name = modName;
            this.side = side;
        }

        Mods(String modid, String modName) {
            this(modid, modName, null);
        }

        Mods(String modid, Side side) {
            this(modid, modid, side);
        }
        private final String mod_name;
        private final String mod_id;
        private final Side side;
        private boolean shouldLoad = true;

        public String getModID() {
            return mod_id;
        }

        public String getModName() {
            return mod_name;
        }

        public boolean isOnClient() {
            return side != Side.SERVER;
        }

        public boolean isOnServer() {
            return side != Side.CLIENT;
        }

        public void loadConfig(Configuration config) {
            shouldLoad = config.get("Integration", "enable" + getModID(), true, "Enable " + getModName() + " Integration.").getBoolean(true);
        }

        public boolean isEnabled() {
            return (Loader.isModLoaded(getModID()) && shouldLoad) || (ModAPIManager.INSTANCE.hasAPI(getModID()) && shouldLoad);
        }
    }
}
