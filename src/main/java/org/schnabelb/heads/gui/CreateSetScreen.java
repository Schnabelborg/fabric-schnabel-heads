package org.schnabelb.heads.gui;

import java.util.UUID;

import org.lwjgl.glfw.GLFW;
import org.schnabelb.heads.Head;
import org.schnabelb.heads.HeadSet;
import org.schnabelb.heads.HeadsMod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class CreateSetScreen extends Screen {
	
	private static final String TITLE = "Create Set";
	private static final String SET_DESCRIPTION_SUGGESTION = "Set description...";
	private static final String SET_NAME_SUGGESTION = "Set name...";
	private ButtonWidget doneButton;
	@SuppressWarnings("unused")
	private ButtonWidget cancelButton;
	private TextFieldWidget setNameTextField;
	private TextFieldWidget setDescriptionTextField;
	private TextFieldWidget itemBackground;
	private Head head;

	protected CreateSetScreen(Head head) {
		super(Text.of(TITLE));
		this.head = head;
	}

	@Override
    protected void init() {
        //this.client.keyboard.setRepeatEvents(true);
        this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.commitAndClose()).dimensions(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20).build());
        this.doneButton.active = false;
        this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20).build());
        this.setNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 86, 100, 240, 20, Text.of("Set Name"));
        this.setNameTextField.setMaxLength(64);
        this.setInitialFocus(this.setNameTextField);
        this.setNameTextField.setTextFieldFocused(true);
        this.setNameTextField.setSuggestion(SET_NAME_SUGGESTION);
        this.setNameTextField.setChangedListener(text -> {
        	if (text == null || text == "") {
                this.setNameTextField.setSuggestion(SET_NAME_SUGGESTION);
                this.doneButton.active = false;
        	} else {
        		this.setNameTextField.setSuggestion("");
        		this.doneButton.active = true;
        	}
        });
        this.addSelectableChild(this.setNameTextField);
        this.setDescriptionTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 86, 124, 240, 40, Text.of("Set Description"));
        this.setDescriptionTextField.setMaxLength(64);
        this.setDescriptionTextField.setSuggestion(SET_DESCRIPTION_SUGGESTION);
        this.setDescriptionTextField.setChangedListener(text -> {
        	if (text == null || text.isBlank()) {
                this.setDescriptionTextField.setSuggestion(SET_DESCRIPTION_SUGGESTION);
        	} else {
        		this.setDescriptionTextField.setSuggestion("");
        	}
        });
        this.addSelectableChild(this.setDescriptionTextField);
        
        this.itemBackground = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 100, 64, 64, Text.of(""));
        this.itemBackground.setEditable(false);
        this.itemBackground.setFocusUnlocked(false);
        this.addDrawableChild(this.itemBackground);
        
    }
	
	@Override
    public void tick() {
        this.setNameTextField.tick();
        this.setDescriptionTextField.tick();
    }

	@Override
    public void resize(MinecraftClient client, int width, int height) {
        String name = this.setNameTextField.getText();
        String description = this.setDescriptionTextField.getText();
        this.init(client, width, height);
        this.setNameTextField.setText(name);
        this.setDescriptionTextField.setText(description);
    }
	
	@Override
    public void removed() {
        //this.client.keyboard.setRepeatEvents(false);
    }
	
	@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && !(this.setNameTextField.getText() == null || this.setNameTextField.getText().isBlank())) {
            this.commitAndClose();
            return true;
        }
        return false;
    }
	
	@Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        AbstractCommandBlockScreen.drawCenteredText(matrices, this.textRenderer, TITLE, this.width / 2, 20, 0xFFFFFF);
        //AbstractCommandBlockScreen.drawTextWithShadow(matrices, this.textRenderer, SET_NAME_TEXT, this.width / 2 - 50, 40, 0xA0A0A0);
        //AbstractCommandBlockScreen.drawTextWithShadow(matrices, this.textRenderer, SET_DESCRIPTION_TEXT, this.width / 2 - 50, 112, 0xA0A0A0);
        this.setNameTextField.render(matrices, mouseX, mouseY, delta);
        this.setDescriptionTextField.render(matrices, mouseX, mouseY, delta);
        this.itemBackground.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        this.itemRenderer.renderGuiItemModel(head.toItemStack(), this.width / 2 - 154, 100, 4);
    }
	
	private void commitAndClose() {
		HeadSet set = new HeadSet(UUID.randomUUID().toString(), this.setNameTextField.getText());
		set.setDescription(this.setDescriptionTextField.getText());
		set.setAuthor(this.client.player.getDisplayName().getString());
		HeadsMod.getSetManager().addSet(set);
		set.save();
		this.close();
		this.client.setScreen(new NameHeadScreen(this.head, set));
	}
	
	
}
