package com.example.openquick.demos.runtimePermissions;

/**
 * Created by kris.shin on 11/13/15.
 *
 */
public interface AppPermissionCallbackHandler {
    void onPermissionGranted();
    void onPermissionDenied();
}
