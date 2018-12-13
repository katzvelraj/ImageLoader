package com.search.velrajp.flickrsearch.di;



import com.search.velrajp.flickrsearch.ui.MainActivity;

import dagger.Component;

/**
 * Dagger2 component
 */

@Component(modules = {DiModule.class})
public interface DiComponent {
    void inject(MainActivity mainActivity);
}
