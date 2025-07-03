package com.motorcycle.dealership.service;

import com.motorcycle.dealership.dto.PaginatedResponse;
import com.motorcycle.dealership.dto.staticcontent.Motorcycle; // <-- THE MISSING IMPORT
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MotorcycleService {

    // You can expand this list based on your old project's data
    private static final List<Motorcycle> MOTORCYCLES = List.of(
        new Motorcycle("BMW S1000RR", "https://thorn-bikes.com/wp-content/uploads/2024/02/GRAPHICS-KIT_BMW-S1000RR-2023_WHITE-BLUE-RED_STANDARD.webp", "205 HP | 999 cc", "The BMW S1000RR is a sport bike initially made by BMW Motorrad to compete in the 2009 Superbike World Championship.", "26,000"),
        new Motorcycle("Yamaha R1", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8TjFpD6jJoZjspzjYSPJlOrUs1QsLeJv0-Q&s", "200 HP | 998 cc", "The Yamaha YZF-R1 is a sport bike motorcycle produced by Yamaha Motor Company since 1998.", "25,000"),
        new Motorcycle("Ducati Panigale V4", "https://www.webbikeworld.com/wp-content/uploads/2021/11/2022-Ducati-Panigale-V4-S-01.jpg", "214 HP | 1,103 cc", "The Panigale V4 is a sport bike with a 1,103 cc desmodromic 90° V4 engine.", "28,000"),
        new Motorcycle("Kawasaki Ninja H2", "https://storage.kawasaki.eu/public/kawasaki.eu/en-EU/model/22MY_Ninja_H2_Carbon_GY1_STU__1_.png", "231 HP | 998 cc", "The Kawasaki Ninja H2 is a 'supercharged supersport' class motorcycle.", "30,000")
    );

    public PaginatedResponse<Motorcycle> getMotorcycles(int page, int limit) {
        if (page < 1) page = 1;
        if (limit < 1) limit = 10;

        long total = MOTORCYCLES.size();
        int startIndex = (page - 1) * limit;

        List<Motorcycle> paginatedList = MOTORCYCLES.stream()
            .skip(startIndex)
            .limit(limit)
            .collect(Collectors.toList());

        return new PaginatedResponse<>(paginatedList, total, page, limit);
    }
}
