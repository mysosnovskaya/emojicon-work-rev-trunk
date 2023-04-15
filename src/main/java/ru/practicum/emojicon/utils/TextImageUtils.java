package ru.practicum.emojicon.utils;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextImageUtils {

    public static TextImage fromString(String text){
        BasicTextImage ret;
        List<List<TextCharacter>> charMatrix = new ArrayList<>();
        Arrays.stream(text.split("\n")).map(String::chars).forEach(line -> {
            charMatrix.add(line.mapToObj(ch -> TextCharacter.fromCharacter((char) ch)).flatMap(Arrays::stream).collect(Collectors.toList()));
        });
        int maxColumns = 0;
        for (List<TextCharacter> line : charMatrix) {
            maxColumns = Math.max(maxColumns, line.size());
        }
        int maxRows = charMatrix.size();
        ret = new BasicTextImage(new TerminalSize(maxColumns, maxRows));
        ret.setAll(TextCharacter.fromCharacter(' ')[0]);
        for (int row = 0; row < charMatrix.size(); row++) {
            List<TextCharacter> line = charMatrix.get(row);
            for (int column = 0; column < line.size(); column++) {
                ret.setCharacterAt(column, row, line.get(column));
            }
        }
        return ret;
    }
}
