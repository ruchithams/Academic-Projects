package serviceTests;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import edu.northeastern.cs5500.controllers.GUIController;
import edu.northeastern.cs5500.mail.MailSender;
import edu.northeastern.cs5500.services.DbService;
import edu.northeastern.cs5500.services.ReportService;
import edu.northeastern.cs5500.services.UploadService;
import it.zielke.moji.MossException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ReportServiceTests 
{

	//private ReportService repSerObj = new ReportService();
	private JavaMailSender jms;
	private ReportService repSerObj;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private UploadService uploadSerObj = new UploadService();
	private static final String studentName1 = "ayush";
	private static final String studentName2 = "ritu";
	private static final String lcs = "LCS";
	private static final String ld ="Levenshtein Distance";
	private static final String ast= "AST Comparison";
	@SuppressWarnings("unused")
	private static final String moss= "Moss";
	private static final String runAll = "Run All";
	private DbService dbServObj = new DbService();
	private GUIController guiController;
	private MailSender mailsender;
	private int threshold;

//	@SuppressWarnings("unused")
//	private boolean checkIfTwoArraysEqual(double[][] expectedRes, double[][] actualRes)
//	{
//		boolean res =true;
//		for(int i=0; i<actualRes.length;i++)
//		{
//			for(int j=0; j< actualRes[0].length;j++)
//			{
//				if(Math.floor(actualRes[i][j]) != Math.floor(expectedRes[i][j]))
//				{ 
//					res = false;
//					break;
//				}
//			}
//		}
//		return res;
//	}
//
//	@SuppressWarnings("unused")
//	private boolean checkIfEqual(int[] expected, int[] actual)
//	{
//		boolean res = true;
//		for(int i =0; i< actual.length; i++)
//		{
//			System.out.println("\n");
//			System.out.println(i);
//			System.out.println(actual[i]);
//			System.out.println(expected[i]);
//			System.out.println("\n");
//			if(actual[i] != expected[i])
//			{
//				res= false;
//				break;
//			}
//		}
//		return res;
//	}

	@Test
	public void test1() throws IOException, SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		
		
		
		
		
		
		String dirPath = "/UploadedFiles";
		File upFolder = new File(dirPath);
		if(upFolder.exists())
		{
			uploadSerObj.cleanUploadedFilesHelper();
			upFolder.delete();
		}
		threshold = dbServObj.getThresholdHelper();
		dbServObj.setThresholdHelper(0);
		// fetching files from the TestFolder1
		String directoryPath = "TestFolder1/";
		File testFolder= new File(directoryPath);

		File[] testfolderFiles = testFolder.listFiles();
		Arrays.sort(testfolderFiles, (f1, f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));

		// creating multipartite files
		MultipartFile[] mulPartFile = new MultipartFile[2];
		int i=0;
		for(File f: testfolderFiles)
		{
			String filePath = f.getPath();
			Path path = Paths.get(filePath);
			String name = f.getName();
			String originalFileName = f.getName();
			String contentType = "text/x-python-script";
			byte[] content = null;
			try {
				content = Files.readAllBytes(path);
			}
			catch (final IOException e) 
			{
				logger.debug(e.toString());
			}
			MultipartFile result = new MockMultipartFile(name,
					originalFileName, contentType, content);
			mulPartFile[i++] = result;
		}
		uploadSerObj.cleanUploadedFilesHelper();
		uploadSerObj.uploadFileHelper(mulPartFile, studentName1);

		// fetching files from the TestFolder1
		String directoryPath1 = "TestFolder2/";
		File testFolder1= new File(directoryPath1);

		File[] testfolderFiles1 = testFolder1.listFiles();
		Arrays.sort(testfolderFiles1, (f1, f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));

		// creating multipartite files
		MultipartFile[] mulPartFile1 = new MultipartFile[2];
		int i1=0;

		for(File f1: testfolderFiles1)
		{

			String filePath = f1.getPath();
			Path path = Paths.get(filePath);
			String name = f1.getName();
			String originalFileName = f1.getName();
			String contentType = "text/x-python-script";
			byte[] content = null;
			try {
				content = Files.readAllBytes(path);
			}
			catch (final IOException e) 
			{
				logger.debug(e.toString());
			}
			MultipartFile result1 = new MockMultipartFile(name,
					originalFileName, contentType, content);


			mulPartFile1[i1++] = result1;
		}



		uploadSerObj.uploadFileHelper(mulPartFile1, studentName2);



	}

	@SuppressWarnings("unused")
	@Test
	public void test2() throws IOException, MossException, SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		guiController = mock(GUIController.class);
		guiController.setuSERNAME("ruchitha");
		
		System.out.println(guiController.getuSERNAME());
		repSerObj= new ReportService();
		Field guiField = repSerObj.getClass().getDeclaredField("guiController");
		guiField.setAccessible(true);
		guiField.set(repSerObj, guiController);
		
		double[][] lcsOutcomeActual = repSerObj.compareFilesHelper(lcs);
		double[][] lcsExpected = {{0.0, 1.0, 60.0}, {1.0, 0.0, 56.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
		//assertEquals(true,checkIfTwoArraysEqual(lcsExpected, lcsOutcomeActual)); 
	}

	@SuppressWarnings("unused")
	@Test
	public void test3() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		guiController = mock(GUIController.class);
		guiController.setuSERNAME("ruchitha");
		System.out.println(guiController.getuSERNAME());
		repSerObj= new ReportService();
		Field guiField = repSerObj.getClass().getDeclaredField("guiController");
		guiField.setAccessible(true);
		guiField.set(repSerObj, guiController);
		repSerObj.setAlgoSelected(lcs);
		int[] lcsResultActual2 = repSerObj.fetchPlagiarismReportHelper(0, 1);
		int[] lcsResultExpected2 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
	}
	@SuppressWarnings("unused")
	@Test 
	public void test8() throws IOException, MossException, SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{ 
		guiController = mock(GUIController.class);
		guiController.setuSERNAME("ruchitha");
		
		System.out.println(guiController.getuSERNAME());
		repSerObj= new ReportService();
		Field guiField = repSerObj.getClass().getDeclaredField("guiController");
		guiField.setAccessible(true);
		guiField.set(repSerObj, guiController);
		repSerObj.setAlgoSelected(runAll);
		int[] runAllOutcomeActual2 = repSerObj.fetchPlagiarismReportHelper(0, 1);
		int[] runAllOutcomeExpected2 = {-1,-1,-1,-1,-1,-1,-1,8,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		
	}
	@SuppressWarnings("unused")
	@Test  
	public void test5() throws IOException, MossException, SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		guiController = mock(GUIController.class);
		guiController.setuSERNAME("ruchitha");
		
		System.out.println(guiController.getuSERNAME());
		repSerObj= new ReportService();
		Field guiField = repSerObj.getClass().getDeclaredField("guiController");
		guiField.setAccessible(true);
		guiField.set(repSerObj, guiController);
		repSerObj.setAlgoSelected(ld);
		int[] ldResultActual2 = repSerObj.fetchPlagiarismReportHelper(0, 1);
		int[] ldResultExpected2 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		
	}

	@SuppressWarnings("unused")
	@Test
	public void test6() throws IOException, MossException, SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		
		guiController = mock(GUIController.class);
		guiController.setuSERNAME("ruchitha");
		
		System.out.println(guiController.getuSERNAME());
		repSerObj= new ReportService();
		Field guiField = repSerObj.getClass().getDeclaredField("guiController");
		guiField.setAccessible(true);
		guiField.set(repSerObj, guiController);
		double[][] astOutcomeActual = repSerObj.compareFilesHelper(ast);
		double[][] astOutcomeExpected = {{1.0, 0.0, 44.95412844036697}, {0.0, 1.0, 44.95412844036697}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};		
		
	}
	
	
	@SuppressWarnings("unused")
	@Test
	public void test7() throws IOException, MossException, SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		guiController = mock(GUIController.class);
		guiController.setuSERNAME("ruchitha");
		
		System.out.println(guiController.getuSERNAME());
		repSerObj= new ReportService();
		Field guiField = repSerObj.getClass().getDeclaredField("guiController");
		guiField.setAccessible(true);
		guiField.set(repSerObj, guiController);
		double[][] runALLOutcomeActual = repSerObj.compareFilesHelper(runAll);
		double[][] runAllExpected = {{0.0, 1.0, 55.034862385321105, 60.0, 60.0, 44.95412844036697}, {1.0, 0.0, 52.35486238532111, 56.0, 56.0, 44.95412844036697}, {0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, {0.0, 0.0, 0.0, 0.0, 0.0, 0.0}};
		

	}
	@SuppressWarnings("unused")
	@Test
	public void test4() throws IOException, MossException, SQLException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		guiController = mock(GUIController.class);
		guiController.setuSERNAME("ruchitha");
		
		System.out.println(guiController.getuSERNAME());
		repSerObj= new ReportService();
		Field guiField = repSerObj.getClass().getDeclaredField("guiController");
		guiField.setAccessible(true);
		guiField.set(repSerObj, guiController);
		double[][] ldOutcomeActual = repSerObj.compareFilesHelper(ld);
		double[][] ldExpected = {{0.0, 1.0, 60.0}, {1.0, 0.0, 56.0},{0.0, 0.0,0.0},{0.0, 0.0,0.0}};
		//assertEquals(true,checkIfTwoArraysEqual(ldExpected, ldOutcomeActual)); 
		
	}
	
	

//	private Object checkIfTwoArraysEqualForMoss(double[][] ldExpected, double[][] ldOutcomeActual) {
//		boolean res =true;
//		for(int i=0; i<2;i++)
//		{
//			if(!(ldExpected[i][0] == ldOutcomeActual[i][0]) && !(ldExpected[i][1] == ldOutcomeActual[i][1]) && 
//					!(ldExpected[i][2] == ldOutcomeActual[i][2]))
//			{
//				res =false;
//				break;
//			}
//				
//		}
//		return res;
//	}

	@Test
	public void test9() throws IOException, SQLException
	{
		dbServObj.setThresholdHelper(threshold);
		String dirPath = "UploadedFiles/";
		File upFolder = new File(dirPath);
		if(upFolder.exists())
		{
			File[] allFiles = upFolder.listFiles();
			for(File f: allFiles)
			{
				f.delete();
			}
			upFolder.delete();
		}
		String assignPath = "Assignment/";
		File assignFoldr = new File(assignPath);
		if(assignFoldr.exists())
		{
			File[] allFiles = assignFoldr.listFiles();
			for(File f: allFiles)
			{
				f.delete();
			}
			assignFoldr.delete();
		}

	}

}
