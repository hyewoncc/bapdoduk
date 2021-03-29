package com.mall.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mall.dao.inquiry.InquiryDao;
import com.mall.util.FileNameUtil;
import com.mall.vo.answer.answerVo;
import com.mall.vo.inquiry.InquiryEmailPhoneVo;
import com.mall.vo.inquiry.InquiryVo;
import com.mall.vo.user.UserVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InquiryController {

	private final InquiryDao dao;
	private final FileNameUtil FileNameUtil;
	private final JavaMailSender javaMailSender;
	
	//페이징 처리 관련 변수
	public static int pageSIZE = 10; // 한 화면에 보여지는 레코드 수
	public static int totalRecord = 0; //전체 레코드 수
	public static int totalPage = 1;// 전체 페이지 수
	
	// 1:1문의 view페이지
	@GetMapping("/inquiry.do")
	public String inquiryForm(Model model) {
		String id = "leewooo";
		// view화면에 보여줄 고객 정보 [고객명,아이디]를 담는다.
		UserVO uv = dao.getMemberInfo(id);
		// 상태 유지 후 inquiry.do로 페이지 이동
		model.addAttribute("uv",uv);
		return "inquiry";
	}
	
	@PostMapping("/inquiry.do")
	public String inquirySubmit(Model model,InquiryVo ivo,String cs_email, String cs_phone,HttpServletRequest request) {
		// 이미지를 저장할 경로
		String path = request.getSession().getServletContext().getRealPath("inquiry");
		// 
		MultipartFile uploadFile = ivo.getUploadFile();
		
		ivo.setCs_fname("");
		
		String fname = uploadFile.getOriginalFilename();
		
		if(fname != null && !"".equals(fname)){
			// 파일 복사 
			try {
				// 파일 내용 가져오기
				byte []data = uploadFile.getBytes();
				// 파일 이름이 중복이 되면 MallimgUtil을 통해서 뒤에 시간을 붙여준다.
				fname = FileNameUtil.FileRename(path, fname);
				
				FileOutputStream fos = new FileOutputStream(path + "/" + fname);
				fos.write(data);
				fos.close();
				ivo.setCs_fname(fname);
			} catch (IOException e) {
				e.printStackTrace();
			}//end catch
			
			System.out.println(ivo.getCs_fname());
			
			int re = dao.insertInquiry(ivo,cs_email,cs_phone);
		}//end if
		
		return "";
	}//inquirySubmit
	
	// 관리자 페이지에서 클라이언트가 문의 남긴 문의 리스트 페이지
	@GetMapping("/inquiryList.do")
	public String inquiryList(Model model, @RequestParam(value="pageNUM",defaultValue = "1") int pageNUM) {
	// 총 게시글 수
	totalRecord = dao.totBoard();
	// 페이지 수 구하기
	totalPage = (int)Math.ceil(totalRecord / (double)pageSIZE);
	// 한화면에 보여질 글 갯수
	int start = (pageNUM-1)*pageSIZE+1;
	int end = start+pageSIZE-1;
	// 한페이지에 보여질 글 갯수 정보를 담고있는 start와 end를 인자로 가지로 조회 후 List에 담는다.
	List<InquiryVo> list=dao.findAllInquiry(start,end);
	// 페이지 수 상태유지
	model.addAttribute("totalPage",totalPage);
	// 고객들의 문의 리스트 상태유지
	model.addAttribute("list",list);
	System.out.println(list.get(0).getCs_mem_id());
	
		return "inquiryList";
	}//inquiryList
	
	//관리자 페이지에 사용자가 문의한 내용 디테일 페이지
	@GetMapping("/inquiryDetail.do")
	public String inquiryDetail(Model model,int cs_no) {
		InquiryVo userIqVo = dao.selectInquiry(cs_no);
		
		model.addAttribute("userIqVo",userIqVo);
		
		return "inquiryDetail";
	}//inquiryDetail
	
	// 고객 답변 메일로 전송하는 메소드
	@PostMapping("/sendQnA.do")
	@ResponseBody
	public String sendQnA(Model model,answerVo answerVo, int cs_no,String cs_mem_id) {
		
		InquiryEmailPhoneVo iep = dao.getMemberEmail(cs_no);

		System.out.println(cs_mem_id+"abc");
		int re = dao.insertAnswer(answerVo,cs_no,cs_mem_id);
		
		
		System.out.println(answerVo);
		
		// 메일 보내기
		SimpleMailMessage mailMessage = new SimpleMailMessage();

		mailMessage.setSubject(answerVo.getAns_title());
		mailMessage.setFrom("jewelrye6");
		mailMessage.setText(answerVo.getAns_content());
		mailMessage.setTo(iep.getCs_email());
		javaMailSender.send(mailMessage);
		
		return "";
	}
}