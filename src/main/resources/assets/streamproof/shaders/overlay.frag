#version 330 core
precision lowp float;

in vec2 uv;
out vec4 color;

uniform sampler2D Sampler0;
uniform vec2 uResolution;

void main() {
    vec4 fbo = texture(Sampler0, uv);
    if (fbo.a < 0.01) {
        discard;
    }

    color = fbo;
}