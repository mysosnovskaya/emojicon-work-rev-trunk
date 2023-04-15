package ru.practicum.emojicon.ui;

import ru.practicum.emojicon.engine.*;

import java.time.Instant;

public abstract class EmojiSplash implements Drawable {

    private final long hideTimeoutMsec;

    private final Runnable onClose;

    private final Instant startTime;

    private boolean removed = false;

    public EmojiSplash(long hideTimeoutMsec, Runnable onClose){
        this.hideTimeoutMsec = hideTimeoutMsec;
        this.onClose = onClose;
        this.startTime = Instant.now();
        this.removed = false;
    }

    @Override
    public final void drawFrame(Frame someFrame) {
        if(removed){
            return;
        }
        RootFrame frame = RootFrame.extend(someFrame);
        EngineContext context = frame.getContext();
        Long dt = Instant.now().toEpochMilli() - startTime.toEpochMilli();
        if(dt >= hideTimeoutMsec){
            removed = true;
            context.removeRoot(this);
            onClose.run();
        } else {
            drawSplash(frame);
        }
    }

    public abstract void drawSplash(Frame frame);
}
