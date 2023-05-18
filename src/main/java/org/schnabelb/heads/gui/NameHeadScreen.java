package org.schnabelb.heads.gui;

import org.lwjgl.glfw.GLFW;
import org.schnabelb.heads.Head;
import org.schnabelb.heads.HeadSet;
import org.schnabelb.heads.HeadsMod;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class NameHeadScreen extends Screen {
	
	private static final String TITLE = "Name Head";
	private static final String SET_NAME_SUGGESTION = "Head name...";
	private ButtonWidget doneButton;
	@SuppressWarnings("unused")
	private ButtonWidget cancelButton;
	private TextFieldWidget headNameTextField;
	private TextFieldWidget itemBackground;
	private Head head;
	private HeadSet set;

	protected NameHeadScreen(Head head, HeadSet set) {
		super(Text.of(TITLE));
		this.head = head;
		this.set = set;
	}

	@Override
    protected void init() {
        //this.client.keyboard.setRepeatEvents(true);
        this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.commitAndClose()).dimensions(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20).build());
        this.doneButton.active = false;
        this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20).build());
        String headName;
        if(this.headNameTextField != null) {
        	headName = this.headNameTextField.getText();
        } else {
        	Head h = HeadsMod.getSetManager().getHeadByUrl(this.head.getURL());
        	headName = h == null ? "" : h.getName();
        }
        this.headNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 120, 140, 240, 20, Text.of("Set Name"));
        this.headNameTextField.setMaxLength(64);
        this.headNameTextField.setText(headName);
        this.setInitialFocus(this.headNameTextField);
        if(this.headNameTextField.getText().isEmpty()) {
        	this.headNameTextField.setSuggestion(SET_NAME_SUGGESTION);        	
        }
        this.headNameTextField.setTextFieldFocused(true);
        this.headNameTextField.setChangedListener(text -> {
        	if (text == null || text == "") {
                this.headNameTextField.setSuggestion(SET_NAME_SUGGESTION);
                this.doneButton.active = false;
        	} else {
        		this.headNameTextField.setSuggestion("");
        		this.doneButton.active = true;
        	}
        });
        this.addSelectableChild(this.headNameTextField);
        
        this.itemBackground = new TextFieldWidget(this.textRenderer, this.width / 2 - 32, 72, 64, 64, Text.of(""));
        this.itemBackground.setEditable(false);
        this.itemBackground.setFocusUnlocked(false);
        this.addDrawableChild(this.itemBackground);
        
    }
	
	@Override
    public void tick() {
        this.headNameTextField.tick();
    }

	/*@Override
    public void resize(MinecraftClient client, int width, int height) {
        String name = this.headNameTextField.getText();
        this.init(client, width, height);
        this.headNameTextField.setText(name);
    }*/
	
	@Override
    public void removed() {
        //this.client.keyboard.setRepeatEvents(false);
    }
	
	@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && !(this.headNameTextField.getText() == null || this.headNameTextField.getText().isBlank())) {
            this.commitAndClose();
            return true;
        }
        return false;
    }
	
	@Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        AbstractCommandBlockScreen.drawCenteredText(matrices, this.textRenderer, TITLE, this.width / 2, 20, 0xFFFFFF);
        this.headNameTextField.render(matrices, mouseX, mouseY, delta);
        this.itemBackground.render(matrices, mouseX, mouseY, delta);
        String author = this.set.getAuthor() == null ? "Unknown" : this.set.getAuthor();
        this.textRenderer.drawWithShadow(matrices, this.set.getDisplayName() + " by " + author, this.width / 2 - 120, 164, 0xA0A0A0);
        super.render(matrices, mouseX, mouseY, delta);
        this.itemRenderer.renderGuiItemModel(head.toItemStack(), this.width / 2 - 32, 72, 4);
    }
	
	private void commitAndClose() {
		this.head.setName(this.headNameTextField.getText());
		set.addHead(head);
		set.save();
		this.close();
	}
	
	
}
