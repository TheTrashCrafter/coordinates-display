package me.boxadactle.coordinatesdisplay.util;

import me.boxadactle.coordinatesdisplay.CoordinatesDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class HudOverlay extends DrawableHelper {
    
    private final MinecraftClient client = MinecraftClient.getInstance();
    
    ModConfig config;

    int w = 0;
    int h = 0;

    public HudOverlay(ModConfig config) {
        this.config = config;
    }
    
    public void updateConfig(ModConfig config) {
        this.config = config;
    }

    public void render(MatrixStack matrices, Vec3d pos, ChunkPos chunkPos, float cameraYaw, RegistryEntry<Biome> biome, int x, int y) {
        try {
            renderOverlay(matrices, pos, chunkPos, cameraYaw, biome, x, y);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void render(MatrixStack matrices, Vec3d pos, ChunkPos chunkPos, float cameraYaw, RegistryEntry<Biome> biome, int x, int y, float scale) {
        try {
            matrices.push();

            matrices.scale(scale, scale, scale);

            renderOverlay(matrices, pos, chunkPos, cameraYaw, biome, x, y);
            matrices.pop();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    public void renderOverlay(MatrixStack matrices, Vec3d pos, ChunkPos chunkPos, float cameraYaw, @Nullable RegistryEntry<Biome> biome, int x, int y) throws NullPointerException {

        DecimalFormat decimalFormat = new DecimalFormat(this.config.roundPosToTwoDecimals ? "0.00" : "0");

        Text xpos = Text.literal(decimalFormat.format(pos.getX())).styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor)));
        Text ypos = Text.literal(decimalFormat.format(pos.getY())).styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor)));
        Text zpos = Text.literal(decimalFormat.format(pos.getZ())).styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor)));

        Text chunkxpos = Text.literal(Integer.toString(chunkPos.x)).styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor)));
        Text chunkzpos = Text.literal(Integer.toString(chunkPos.z)).styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor)));

        Text biometext;
        if (this.client.world != null) {
            biometext = this.config.renderBiome ? Text.literal(biome != null ? ModUtils.parseIdentifier(ModUtils.getBiomeString(biome)) : "Plains").styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor))) : Text.literal("");
        } else
            biometext = Text.literal("Plains").styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor)));

        Text dtext = Text.translatable("hud.coordinatesdisplay.direction." + ModUtils.getDirectionFromYaw(MathHelper.wrapDegrees(cameraYaw))).styled((style -> style.withColor(CoordinatesDisplay.CONFIG.dataColor)));

        Text xText = Text.translatable("hud.coordinatesdisplay.x", xpos);
        Text yText = Text.translatable("hud.coordinatesdisplay.y", ypos);
        Text zText = Text.translatable("hud.coordinatesdisplay.z", zpos);

        Text chunkX = Text.translatable("hud.coordinatesdisplay.chunk.x", chunkxpos);
        Text chunkZ = Text.translatable("hud.coordinatesdisplay.chunk.z", chunkzpos);

        Text directionText = this.config.renderDirection ? Text.translatable("hud.coordinatesdisplay.direction", dtext) : Text.literal("");

        Text biomeText = this.config.renderBiome ? Text.translatable("hud.coordinatesdisplay.biome", biometext) : Text.literal("");

        int th = 10;
        int p = this.config.padding;
        int tp = this.config.textPadding;

        w = p + ModUtils.getLongestLength(xText, yText, zText) +
                (this.config.renderChunkData ? tp + ModUtils.getLongestLength(chunkX, chunkZ) :
                        ModUtils.getLongestLength(xText, yText, zText, biomeText, directionText) - ModUtils.getLongestLength(xText, yText, zText)) + p;
        h = p + (th * 3) + (CoordinatesDisplay.CONFIG.renderDirection ? tp + th : 0) +
                (CoordinatesDisplay.CONFIG.renderBiome ? (CoordinatesDisplay.CONFIG.renderDirection ? 0 : tp) + th : 0) + p;

        if (this.config.renderBackground) {
            if (this.getTextRenderer().getWidth(biomeText) > w || this.getTextRenderer().getWidth(directionText) > w) {
                fill(matrices, x, y, x + p + ModUtils.getLongestLength(biomeText, directionText), y + h, ModUtils.TRANSPARENT_GRAY);
            } else {
                fill(matrices, x, y, x + w, y + h, ModUtils.TRANSPARENT_GRAY);
            }
        }


        this.getTextRenderer().drawWithShadow(matrices, xText, x + p, y + p, CoordinatesDisplay.CONFIG.definitionColor);
        this.getTextRenderer().drawWithShadow(matrices, yText, x + p, y + p + th, CoordinatesDisplay.CONFIG.definitionColor);
        this.getTextRenderer().drawWithShadow(matrices, zText, x + p, y + p + (th * 2), CoordinatesDisplay.CONFIG.definitionColor);

        if (this.config.renderChunkData) {
            this.getTextRenderer().drawWithShadow(matrices, chunkX, x + p + ModUtils.getLongestLength(xText, yText, zText) + tp, y + p, CoordinatesDisplay.CONFIG.definitionColor);
            this.getTextRenderer().drawWithShadow(matrices, chunkZ, x + p + ModUtils.getLongestLength(xText, yText, zText) + tp, y + p + (th), CoordinatesDisplay.CONFIG.definitionColor);
        }

        if (this.config.renderDirection) {
            this.getTextRenderer().drawWithShadow(matrices, directionText, x + p, y + p + (th * 3) + tp, CoordinatesDisplay.CONFIG.definitionColor);
        }

        if (this.config.renderBiome) {
            this.getTextRenderer().drawWithShadow(matrices, biomeText, x + p, y + p + (th * 3) + tp + (this.config.renderDirection ? th : 0), CoordinatesDisplay.CONFIG.definitionColor);
        }
    }

    public TextRenderer getTextRenderer() {
        return this.client.textRenderer;
    }
    
}
