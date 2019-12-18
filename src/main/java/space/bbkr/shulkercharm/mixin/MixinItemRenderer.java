package space.bbkr.shulkercharm.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.bbkr.shulkercharm.hooks.CustomDurabilityItem;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
	private void renderCustomDurabilityBar(TextRenderer textRenderer, ItemStack stack, int x, int y, String amount, CallbackInfo info) {
		if (stack.getItem() instanceof CustomDurabilityItem) {
			CustomDurabilityItem durab = (CustomDurabilityItem)stack.getItem();
			GlStateManager.disableDepthTest();
			GlStateManager.disableTexture();
			GlStateManager.disableAlphaTest();
			GlStateManager.disableBlend();

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder builder = tessellator.getBufferBuilder();

			float progress = ((float) durab.getDurability(stack)) / ((float) durab.getMaxDurability(stack));
			int durability = (int) (13 * progress);
			int color = durab.getDurabilityColor(stack);

			this.renderGuiQuad(builder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
			this.renderGuiQuad(builder, x + 2, y + 13, durability, 1, color >> 16 & 255, color >> 8 & 255, color & 255, 255);

			GlStateManager.enableBlend();
			GlStateManager.enableAlphaTest();
			GlStateManager.enableTexture();
			GlStateManager.enableDepthTest();
		}
	}
}
