package ru.practicum.emojicon.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.screen.Screen;
import ru.practicum.emojicon.engine.Frame;
import ru.practicum.emojicon.engine.RootFrame;
import ru.practicum.emojicon.utils.TextImageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmojiSplashCatFace extends EmojiSplash {

    private static final String KITTY_0 = "" +
            "          .__....._             _.....__,\n" +
            "            .\": o :':         ;': o :\".\n" +
            "            `. `-' .'.       .'. `-' .'\n" +
            "              `---'             `---'\n" +
            "\n" +
            "    _...----...      ...   ...      ...----..._\n" +
            " .-'__..-\"\"'----    `.  `\"`  .'    ----'\"\"-..__`-.\n" +
            "'.-'   _.--\"\"\"'       `-._.-'       '\"\"\"--._   `-.`\n" +
            "'  .-\"'                  :                  `\"-.  `\n" +
            "  '   `.              _.'\"'._              .'   `\n" +
            "        `.       ,.-'\"       \"'-.,       .'\n" +
            "          `.                           .'\n" +
            "            `-._                   _.-'\n" +
            "                `\"'--...___...--'\"`";

    private static final String BANNER_0 = "" +
            "\n" +
            "▄███▄   █▀▄▀█ ████▄   ▄▄▄▄▄ ▄█ ▄█▄    ████▄    ▄   \n" +
            "█▀   ▀  █ █ █ █   █ ▄▀  █   ██ █▀ ▀▄  █   █     █  \n" +
            "██▄▄    █ ▄ █ █   █     █   ██ █   ▀  █   █ ██   █ \n" +
            "█▄   ▄▀ █   █ ▀████  ▄ █    ▐█ █▄  ▄▀ ▀████ █ █  █ \n" +
            "▀███▀      █          ▀      ▐ ▀███▀        █  █ █ \n" +
            "          ▀                                 █   ██ \n" +
            "                                                   \n";

    private TextImage catImageCache;
    private TextImage bannerImageCache;

    public EmojiSplashCatFace(long hideTimeoutMsec, Runnable onClose) {
        super(hideTimeoutMsec, onClose);
    }

    @Override
    public void drawSplash(Frame someFrame) {
        RootFrame frame = RootFrame.extend(someFrame);
        Screen screen = frame.getScreen();
        TextGraphics text = screen.newTextGraphics();
        TextImage imageCat = getCatImage();
        TextImage imageBanner = getBannerImage();
        //center on screen wide
        TerminalPosition ltCat = new TerminalPosition((screen.getTerminalSize().getColumns() / 2) - (imageCat.getSize().getColumns() / 2), 1);
        TerminalPosition ltBanner = new TerminalPosition((screen.getTerminalSize().getColumns() / 2) - (imageBanner.getSize().getColumns() / 2), imageCat.getSize().getRows() + 1);
        text.drawImage(ltCat, imageCat);
        text.drawImage(ltBanner, imageBanner);
    }

    private TextImage getBannerImage() {
        if(bannerImageCache != null){
            return bannerImageCache;
        }
        bannerImageCache = TextImageUtils.fromString(BANNER_0);
        return bannerImageCache;
    }

    private TextImage getCatImage() {
        if(catImageCache != null){
            return catImageCache;
        }
        catImageCache = TextImageUtils.fromString(KITTY_0);
        return catImageCache;
    }
}
