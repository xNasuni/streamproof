#version 330 core
precision lowp float;

in vec2 uv;
out vec4 color;

uniform vec2 uResolution;
uniform float uTime;
uniform vec4 uColor;

void main() {
    color = uColor;
}