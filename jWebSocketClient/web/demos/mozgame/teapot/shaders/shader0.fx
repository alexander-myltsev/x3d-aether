/*
 * Copyright 2010, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

attribute vec4 position;
attribute vec3 texCoord0;
attribute vec4 tangent;
attribute vec4 binormal;
attribute vec4 normal;

varying vec4 v_position;
varying vec2 v_texCoord0;
varying vec3 v_worldEyeVec;
varying vec3 v_worldNormal;
varying vec3 v_worldTangent;
varying vec3 v_worldBinormal;

uniform mat4 world;
uniform mat4 worldInverseTranspose;
uniform mat4 worldViewProjection;
uniform mat4 viewInverse;

void main() {
  v_position = worldViewProjection * position;
  v_texCoord0.xy = texCoord0.xy;
  v_worldNormal = (worldInverseTranspose * normal).xyz;
  v_worldTangent = (worldInverseTranspose * tangent).xyz;
  v_worldBinormal = (worldInverseTranspose * binormal).xyz;
  vec3 worldPos = (world * position).xyz;
  v_worldEyeVec = normalize(worldPos - viewInverse[3].xyz);
  gl_Position = v_position;
}

// #o3d SplitMarker

varying vec4 v_position;
varying vec2 v_texCoord0;
varying vec3 v_worldEyeVec;
varying vec3 v_worldNormal;
varying vec3 v_worldTangent;
varying vec3 v_worldBinormal;

uniform float BumpHeight;
uniform sampler2D NormalSampler;
uniform samplerCube EnvSampler;

void main() {
  vec2 bump = BumpHeight *
      (texture2D(NormalSampler, v_texCoord0.xy).xy * 2.0 - 1.0);
  vec3 normal = normalize(v_worldNormal);
  vec3 tangent = normalize(v_worldTangent);
  vec3 binormal = normalize(v_worldBinormal);

  vec3 nb = normal + bump.x * tangent + bump.y * binormal;
  nb = normalize(nb);
  vec3 worldEyeVec = normalize(v_worldEyeVec);
  vec3 lookup = reflect(worldEyeVec, nb);
  gl_FragColor = textureCube(EnvSampler, lookup);
}

// #o3d MatrixLoadOrder RowMajor

