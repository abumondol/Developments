sub_count = length(data);
exclude_offset = 3*16;

for s =1:sub_count
    fprintf('Subject %d\n', s);    
    labels = segments(s).labels(:,1);
    annot_to_min_dist = segments(s).labels(:,3);
    mp = segments(s).min_points(:,1);
    count = length(labels);
    
    for i=1:count
        if labels(i)>0 %&& labels(i)<400
            for j=i-1:-1:1
                if mp(i)-mp(j)>exclude_offset || labels(j)>0
                    break
                end
                labels(j) = -1;
            end
            
            for j=i+1:count
                if mp(j)-mp(i)>exclude_offset || labels(j)>0
                    break
                end
                labels(j) = -1;
            end
            
            if annot_to_min_dist(i)>=24
                labels(j) = -3;
            end            
        end
        
        if labels(i)>400
            labels(i) = -2;
        end
    end
    segments(s).min_points = [mp, segments(s).labels(:,1), labels];
end

save('segments', 'segments');

