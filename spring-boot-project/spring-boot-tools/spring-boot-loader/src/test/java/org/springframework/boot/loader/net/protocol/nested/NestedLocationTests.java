/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.loader.net.protocol.nested;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.boot.loader.net.protocol.Handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link NestedLocation}.
 *
 * @author Phillip Webb
 */
class NestedLocationTests {

	@TempDir
	File temp;

	@BeforeAll
	static void registerHandlers() {
		Handlers.register();
	}

	@Test
	void createWhenPathIsNullThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new NestedLocation(null, "nested.jar"))
			.withMessageContaining("'path' must not be null");
	}

	@Test
	void createWhenNestedEntryNameIsNullThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new NestedLocation(Path.of("test.jar"), null))
			.withMessageContaining("'nestedEntryName' must not be empty");
	}

	@Test
	void createWhenNestedEntryNameIsEmptyThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new NestedLocation(Path.of("test.jar"), null))
			.withMessageContaining("'nestedEntryName' must not be empty");
	}

	@Test
	void fromUrlWhenUrlIsNullThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> NestedLocation.fromUrl(null))
			.withMessageContaining("'url' must not be null");
	}

	@Test
	void fromUrlWhenNotNestedProtocolThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> NestedLocation.fromUrl(new URL("file://test.jar")))
			.withMessageContaining("must use 'nested' protocol");
	}

	@Test
	void fromUrlWhenNoPathThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> NestedLocation.fromUrl(new URL("nested:")))
			.withMessageContaining("'path' must not be empty");
	}

	@Test
	void fromUrlWhenNoSeparatorThrowsExceptiuon() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> NestedLocation.fromUrl(new URL("nested:test.jar!nested.jar")))
			.withMessageContaining("'path' must contain '/!'");
	}

	@Test
	void fromUrlReturnsNestedLocation() throws Exception {
		File file = new File(this.temp, "test.jar");
		NestedLocation location = NestedLocation
			.fromUrl(new URL("nested:" + file.getAbsolutePath() + "/!lib/nested.jar"));
		assertThat(location.path()).isEqualTo(file.toPath());
		assertThat(location.nestedEntryName()).isEqualTo("lib/nested.jar");
	}

	@Test
	void fromUriWhenUrlIsNullThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> NestedLocation.fromUri(null))
			.withMessageContaining("'uri' must not be null");
	}

	@Test
	void fromUriWhenNotNestedProtocolThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> NestedLocation.fromUri(new URI("file://test.jar")))
			.withMessageContaining("must use 'nested' scheme");
	}

	@Test
	void fromUriWhenNoSeparatorThrowsExceptiuon() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> NestedLocation.fromUri(new URI("nested:test.jar!nested.jar")))
			.withMessageContaining("'path' must contain '/!'");
	}

	@Test
	void fromUriReturnsNestedLocation() throws Exception {
		File file = new File(this.temp, "test.jar");
		NestedLocation location = NestedLocation
			.fromUri(new URI("nested:" + file.getAbsolutePath() + "/!lib/nested.jar"));
		assertThat(location.path()).isEqualTo(file.toPath());
		assertThat(location.nestedEntryName()).isEqualTo("lib/nested.jar");
	}

}
