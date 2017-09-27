varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;
uniform float darknessIntensity;
uniform float lighting;

void main() {
        vec4 color = (texture2D(u_sampler2D, v_texCoord0) * v_color);
		if(lighting == 0.0) {
			if(color.r == 0.0 && color.b == 0.0 && color.g == 0.0) {
				color.a = 0.4 - ((darknessIntensity / 0.8)*0.4);
				color.r = 0.0;	
				color.b = 0.0;
				color.g = 0.0;
			} else {
				color.a = 0.0;
			}
		} else {
			if(color.r != 0.0 && color.b != 0.0 && color.g != 0.0) {
				color.a = 0.0;
			}
		}

		gl_FragColor = color;
}
