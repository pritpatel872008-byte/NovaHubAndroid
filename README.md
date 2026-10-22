# Nova Hub

Nova Hub is an Android device-security companion with a clean, One UI-inspired interface. It reads device information from Android system APIs, opens official Android settings, and uses AndroidX BiometricPrompt for system-managed authentication.

## Build an APK
GitHub Actions builds a debug APK on pushes to `main` and manual workflow dispatch. Open **Actions → Build Nova Hub APK** and download the `NovaHub-debug-apk` artifact after a successful run.

## Safety and scope
- This is not Samsung software and does not install Samsung One UI or firmware.
- It cannot promise or force Android updates; update eligibility is controlled by the device maker/carrier.
- The app never asks for or stores the phone's PIN or fingerprint images.
- The pass/payment feature is not implemented; this app is not a payment wallet.
- Device details are read from Android system APIs; hardware-dependent behavior must be verified on a physical device.

## Package
`com.novahub.android`
