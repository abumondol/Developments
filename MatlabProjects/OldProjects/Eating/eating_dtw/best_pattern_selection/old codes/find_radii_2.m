%clear all
load('distances');
load('pos_indices');

poscount = length(pos_indices, 1);
results = zeros(pos_count, 2);

for i=1:poscount    
    for i=1:poscount        
        sid = pos_indices_remain(i,1);
        index = pos_indices_remain(i,2);
        dist = distances(sid).dist;
        
        d = dist(dist(:,1)==index, [9, 16, 4, 2, 3]); %distance 3s left, dist 2s right, label, sub id, index
        d = [d(:,1)+d(:,2), d(:,3:end)];
        d = sortrows(d);        
        len = length(d);
        
        negcount = 0;        
        last_bdist = 0;
        last_covered = 0;
        
        for j = 1:len
            if d(j, 2) == 0                
                negcount = negcount + 1;                
                if negcount == 1
                    last_covered = j - negcount;
                    if j == 1
                        last_bdist = d(j, 1)/2;
                        last_covered_list = [];
                    else
                        last_bdist = d(j-1,1) + (d(j, 1) - d(j-1, 1))/2;
                        last_covered_list = d(1:j-1, :);
                    end
                    
                else
                    covered = j-negcount;
                    if covered - last_covered >= 5
                        last_covered = j - negcount;
                        last_bdist = d(j-1,1) + (d(j, 1) - d(j-1, 1))/2;
                        last_covered_list = d(1:j-1, :);
                    else
                        negcount = negcount-2;
                        break;
                    end
                    
                end
            end

        end
       
        if best_sid ==0 || last_covered + best_negcount > best_covered + negcount || (last_covered + best_negcount == best_covered + negcount && last_bdist > best_bdist)
            best_sid = sid;
            best_index = index;
            best_covered = last_covered;
            best_negcount = negcount;
            best_bdist = last_bdist;
            best_covered_list = last_covered_list;
        end        
        
    end % current pos list loop   
    
    res = [best_sid, best_index, best_covered+1, best_negcount, best_bdist];
    results = [results; res]
    if ~isempty(best_covered_list)
        best_covered_list = best_covered_list(best_covered_list(:,2)>0, 3:4);    
    end
    best_covered_list = [best_covered_list; best_sid, best_index];    
    best_covered_list = sortrows(best_covered_list);
    pos_indices_remain = remove_from_pos_indices(pos_indices_remain, best_covered_list); 
    distances = remove_from_distances(distances, best_covered_list);    
    
    fprintf('Covered:%d, Total covered: %d, Remain: %d\n', size(best_covered_list, 1), sum(results(:,3)), size(pos_indices_remain, 1));
end % while loop

results_3_2 = results;
save('results_3_2', 'results_3_2');
