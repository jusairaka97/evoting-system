package com.collegeelection.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.collegeelection.model.Candidate;
import com.collegeelection.model.Election;
import com.collegeelection.model.User;
import com.collegeelection.repository.UserRepository;
import com.collegeelection.service.CandidateService;
import com.collegeelection.service.ElectionService;
import com.collegeelection.service.VoteService;

@Controller
@RequestMapping("/voter")
public class VoterController {

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VoteService voteService;
	@Autowired
	private ElectionService electionService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

//-------------------------------------------------------------------------------------
	@GetMapping("/voter_dashboard")
	public String voterDashboard(Model model, Authentication authentication) {
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);

		boolean electionEnded = electionService.isElectionEnded();
		boolean electionLive = electionService.isElectionLive();
		boolean hasVoted = voteService.hasUserVoted(user);
		Election liveElection = electionService.getLiveElection();

		model.addAttribute("electionEnded", electionEnded);
		model.addAttribute("hasVoted", hasVoted);
		model.addAttribute("electionLive", electionLive);
		model.addAttribute("liveElection", liveElection);
		model.addAttribute("username", user.getUsername());
		return "voter/voter_dashboard";
	}

	@GetMapping("/voter_vote")
	public String showVotePage(Model model, Authentication authentication) {
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);

		if (voteService.hasUserVoted(user)) {
			return "redirect:/voter/voter_status";
		}
		Election election = electionService.getLiveElection();
		model.addAttribute("liveElection", election);
		Map<String, List<Candidate>> candidatesByPosition = candidateService.getCandidatesGroupedByPosition();
		model.addAttribute("candidatesByPosition", candidatesByPosition);
		// model.addAttribute("candidates", candidateService.getAllCandidates());
		return "voter/voter_vote";
	}

	@PostMapping("/voter_vote")
	public String submitVote(@RequestParam Map<String, String> voteData, Authentication authentication) {
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);

		boolean allVotesSuccessful = true;

		for (Map.Entry<String, String> entry : voteData.entrySet()) {
			try {
				Long candidateId = Long.parseLong(entry.getValue());
				boolean voted = voteService.castVote(user, candidateId);
				if (!voted) {
					allVotesSuccessful = false;
				}
			} catch (NumberFormatException e) {
				allVotesSuccessful = false;
			}
		}

		if (allVotesSuccessful) {
			return "redirect:/voter/voter_status";
		} else {
			return "redirect:/voter/voter_vote?error=true";
		}
	}

	@GetMapping("/voter_status")
	public String showVoteStatusPage(Model model, Authentication authentication) {
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);

		if (voteService.hasUserVoted(user)) {
			model.addAttribute("message", "You have already cast your vote.");
		} else {
			model.addAttribute("message", "You have not voted yet.");
		}

		return "voter/voter_status";
	}

//=================================================================
	@GetMapping("/voter_results")
	public String viewResults(Model model) {
		List<Object[]> results = voteService.getVotingResults();

		int totalVotes = results.stream().mapToInt(r -> ((Long) r[1]).intValue()).sum();

		// Prepare labels and data for the chart
		List<String> labels = results.stream().map(r -> (String) r[0]).toList();

		List<Integer> data = results.stream().map(r -> ((Long) r[1]).intValue()).toList();

		model.addAttribute("results", results);
		model.addAttribute("totalVotes", totalVotes);
		model.addAttribute("labels", labels);
		model.addAttribute("data", data);

		return "voter/voter_results"; 
	}

	// ----------------------------------------------------------------------------
	@GetMapping("/voter_profile")
	public String showProfileForm(Model model, Authentication authentication) {
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);
		model.addAttribute("user", user);
		return "voter/voter_profile";
	}

	@PostMapping("/voter_profile")
	public String updateProfile(@RequestParam String username, @RequestParam(required = false) String password,
			Authentication authentication) {

		String userName = authentication.getName();
		User user = userRepository.findByUsername(username);

		user.setUsername(userName);

		if (password != null && !password.isEmpty()) {
			user.setPassword(passwordEncoder.encode(password));
		}

		userRepository.save(user);
		return "redirect:/voter/voter_dashboard";
	}

	// -----------------------------------------------------------

}
