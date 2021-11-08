#version 300 es

// uniform sampler2D DST_CHANNEL;
vec4 COLOR;

//<indigo-fragment>
void fragment() {
 //vec2 offset = vec2(0.5, 0.5);
 // COLOR = vec4(COLOR, 1.0);
 // COLOR = texture(DST_CHANNEL, offset);
 // float y = u_mouse.y / u_resolution.y;
 COLOR = vec4(1.0, 0, 0, 1.0);
}
//</indigo-fragment>