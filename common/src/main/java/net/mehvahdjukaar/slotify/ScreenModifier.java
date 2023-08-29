package net.mehvahdjukaar.slotify;

import java.util.ArrayList;
import java.util.List;

public record ScreenModifier(int titleX, int titleY, int labelX, int labelY,
                           List<SimpleSprite> sprites) {

    public ScreenModifier(GuiModifier original){
        this(original.titleX(), original.titleY(), original.labelX(), original.labelY(), new ArrayList<>(original.sprites()));
    }

    public ScreenModifier merge(ScreenModifier other){
        this.sprites.addAll(other.sprites);
        return this;
    }
}
