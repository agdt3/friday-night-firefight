#version 300 es

vec2 UV;
vec4 CHANNEL_0;
float TIME;

//<indigo-fragment>
float plot(vec2 st) {
  return smoothstep(0.02, 0.0, abs(st.y - st.x));
}

void fragment() {
  //COLOR = vec4(abs(sin(TIME)), CHANNEL_0.y, CHANNEL_0.z, 1.0);
  //float y = UV.y;
  //vec3 color = vec3(y);
  //float pct = plot(UV);
  //color = (1.0-pct) * color + pct * vec3(0.0,1.0,0.0);

  //COLOR = vec4(color, 1.0);
  // COLOR = vec4(UV.x, UV.y, 0.0, 1.0);

  //vec3 color = vec3(0.0);

  /*
  vec2 topLeft = step(vec2(0.1), UV);
  float pct = topLeft.x * topLeft.y;

  vec3 color = vec3(pct);
  COLOR = vec4(color, 1.0);
  */

  // COLOR = vec4(abs(sin(u_time)), 1.0, 0, 1.0);

}
//</indigo-fragment>