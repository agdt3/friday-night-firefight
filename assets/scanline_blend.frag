#version 300 es

vec4 COLOR;
vec4 SRC;
vec2 UV;
vec2 SIZE;

//<indigo-fragment>
float fmin = 0.7;

float scanline(float position) {
 // Produces square wave value that is 0 or 1
 float square_wave = mod(position, 2.0);
 // Returns value within [0.7, 1.0] (i.e. 0.7 + 0 to 0.3)
 return fmin + (1.0 - fmin) * square_wave;
}

float square_function(float position) {
 return mod(position, 2.0);
}

float square_function2(float position) {
 // Sin function returns a value in (1.0, -1.0)
 // Sign function returns -1 or 0 or 1 depending on whether value is < 0, =0, or > 0
 // Divide by 2.0 to return -0.5 or 0 or 0.5
 // Add 0.5 to reutn 0 or 0.5 or 1
 // sign function return 0 or 1
 return sign(sign(sin(position)) / 2.0 + 0.5);
}

float sine_function(float position) {
 // Get sine of x or y value, returns value in (1.0, -1.0)
 // Divide by 2.0 to constrain to (0.5, -0.5)
 // Add 0.5 to constrain to (1.0, 0.0)
 return sin(position) / 2.0 + 0.5;
}

float triangle_function(float position, float period) {
 // returns triangle wave within [0,1]
 return 2.0 * abs(position / period - floor(position / period + 0.5));
}

void fragment() {
 // COLOR = vec4(SRC.rgb * square_function(UV.y * SIZE.y, 2.0), SRC.a);
 // COLOR = vec4(SRC.rgb * sine_function(UV.y * SIZE.y), SRC.a);
 // COLOR = vec4(SRC.rgb * square_function2(UV.y * SIZE.y), SRC.a);
 // COLOR = vec4(SRC.rgb * triangle_function(UV.y * SIZE.y, 8.0), SRC.a);
 COLOR = vec4(SRC.rgb * scanline(UV.y * SIZE.y), SRC.a);
}
//</indigo-fragment>