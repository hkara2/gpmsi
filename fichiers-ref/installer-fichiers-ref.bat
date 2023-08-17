
setlocal enableextensions

if not exist %USERPROFILE%\.gpmsi md %USERPROFILE%\.gpmsi
if not exist %USERPROFILE%\.gpmsi\ccam md %USERPROFILE%\.gpmsi\ccam
if not exist %USERPROFILE%\.gpmsi\cim md %USERPROFILE%\.gpmsi\cim

copy ccam\* %USERPROFILE%\.gpmsi\ccam
copy cim\* %USERPROFILE%\.gpmsi\cim

