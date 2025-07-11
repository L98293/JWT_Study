package samdi.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samdi.demo.Domain.Member;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
}
