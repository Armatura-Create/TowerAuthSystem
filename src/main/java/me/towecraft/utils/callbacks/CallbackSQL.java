package me.towecraft.utils.callbacks;

public interface CallbackSQL<Reply> {
    void done(final Reply data);

    void error(final Exception ex);
}
