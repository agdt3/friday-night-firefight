#version 300 es
vec2 UV;

//<indigo-vertex>
void vertex() {
    VERTEX = vec4(UV, 0.0, 0.0);
}
//</indigo-vertex>