package services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import models.Tutorial;

@Service
public class TutorialService {
	
	private final List<Tutorial> tutorials = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		tutorials.add(new Tutorial(1l, "Tutorial1", "Tutorial 1 Description", 1l, 1l));
		tutorials.add(new Tutorial(2l, "Tutorial2", "Tutorial 2 Description", 2l, 1l));
		tutorials.add(new Tutorial(3l, "Tutorial3", "Tutorial 3 Description", 1l, 2l));
		tutorials.add(new Tutorial(4l, "Tutorial4", "Tutorial 4 Description", 2l, 2l));
	}
	
	public Tutorial getById(Long tutorialId) {
		return tutorials.stream().filter((tutorial) -> tutorial.getId() == tutorialId).findFirst().get();
	}
	
	public List<Tutorial> getAllTutorials() {
		return tutorials;
	}
}