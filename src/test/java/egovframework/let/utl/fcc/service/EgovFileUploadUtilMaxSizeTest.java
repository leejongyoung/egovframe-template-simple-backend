package egovframework.let.utl.fcc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;


/** maxFileSize 한도가 저장 전에 적용되는지 검증한다. 네트워크·DB 없이 임시 폴더만 사용한다. */
class EgovFileUploadUtilMaxSizeTest {

	private static final String WHITE_LIST = ".txt.png";

	private static MockMultipartHttpServletRequest request(MockMultipartFile... files) {
		MockMultipartHttpServletRequest req = new MockMultipartHttpServletRequest();
		for (MockMultipartFile f : files) {
			req.addFile(f);
		}
		return req;
	}

	private static MockMultipartFile file(String field, String name, int size) {
		return new MockMultipartFile(field, name, "text/plain", new byte[size]);
	}

	private static long savedFileCount(Path dir) throws IOException {
		try (Stream<Path> s = Files.walk(dir)) {
			return s.filter(Files::isRegularFile).count();
		}
	}

	@Test
	@DisplayName("한도와 같은 크기와 더 작은 파일은 저장된다")
	void fileAtOrBelowLimitIsSaved(@TempDir Path dir) throws Exception {
		List<?> saved = EgovFileUploadUtil.uploadFilesExt(
			request(file("a", "a.txt", 10), file("b", "b.txt", 5)), dir.toString(), 10, WHITE_LIST);

		assertEquals(2, saved.size());
		assertEquals(2, savedFileCount(dir));
	}

	@Test
	@DisplayName("한도를 1바이트라도 넘으면 저장 전에 거절한다")
	void fileOverLimitIsRejectedBeforeSaving(@TempDir Path dir) throws Exception {
		assertThrows(SecurityException.class, () -> EgovFileUploadUtil.uploadFilesExt(
			request(file("a", "a.txt", 11)), dir.toString(), 10, WHITE_LIST));

		assertEquals(0, savedFileCount(dir));
	}

	@Test
	@DisplayName("정상 파일과 초과 파일이 섞이면 아무것도 저장하지 않는다")
	void mixedBatchSavesNothing(@TempDir Path dir) throws Exception {
		assertThrows(SecurityException.class, () -> EgovFileUploadUtil.uploadFilesExt(
			request(file("ok", "ok.txt", 5), file("big", "big.txt", 50)), dir.toString(), 10, WHITE_LIST));

		assertEquals(0, savedFileCount(dir));
	}

	@Test
	@DisplayName("한도가 0 이하이면 제한하지 않는다 (기존 동작 유지)")
	void nonPositiveLimitMeansUnlimited(@TempDir Path dir) throws Exception {
		assertEquals(1, EgovFileUploadUtil.uploadFilesExt(
			request(file("a", "a.txt", 1000)), dir.toString(), 0, WHITE_LIST).size());
		assertEquals(1, EgovFileUploadUtil.uploadFilesExt(
			request(file("b", "b.txt", 1000)), dir.toString(), -1, WHITE_LIST).size());
		assertTrue(savedFileCount(dir) >= 2);
	}
}
