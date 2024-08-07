if [[ -z "${ANACONDA3_HOME}" ]]; then
    ANACONDA3_HOME=/opt/anaconda3
fi

java --enable-native-access=ALL-UNNAMED \
    --enable-preview --source=22 \
    -Djava.library.path=${ANACONDA3_HOME}/lib \
    PythonMain.java
