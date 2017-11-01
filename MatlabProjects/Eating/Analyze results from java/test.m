counts = [];
for i = 1:11
    sess_count = 2;
    if i == 10
        sess_count = 4;
    end
        
    for j = 1:sess_count
        m = data(i).session(j).meals;
        c = [];
        for k=1:5
            c = [c, sum(m(:,end)==k)];
        end
        counts = [counts; c];
    end
end

sum(counts)