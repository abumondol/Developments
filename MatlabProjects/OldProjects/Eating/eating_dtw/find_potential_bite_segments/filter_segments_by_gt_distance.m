sub_count = length(data);
%offset_allowed_for_pos = 32;
%offset_required_for_neg = 48;

for s =1:sub_count            
    segs = segments(s).segs_by_minx;    
    a = data(s).annots;    
    if isempty(a)
        segments(s).segs_selected_include_drink = segs;
        segments(s).segs_selected = segs;        
                
        segments(s).segs_eat_count = 0; 
        segments(s).segs_drink_count = 0;  
        segments(s).segs_neg_count = size(segs, 1);     

        segments(s).gt_eat_count = 0;
        segments(s).gt_drink_count = 0;
        continue
    end
    
    cond1 = segs(:,3) > 0 & segs(:, 4) <= offset_allowed_for_pos;    
    cond2 = segs(:, 3) == 0 & segs(:, 6) >= offset_required_for_neg;    
    segs = segs(cond1 | cond2, :);
    
    segments(s).segs_selected_include_drink = segs;
    segments(s).segs_selected = segs(segs(:,3)<400, :);    
    segments(s).segs_eat_count = sum(segs(:, 3)>0 & segs(:, 3)<400); 
    segments(s).segs_drink_count = sum(segs(:, 3)>=400 & segs(:, 3)<1000);  
    segments(s).segs_neg_count = sum(cond2);     
    
    segments(s).gt_eat_count = data(s).eat_annot_count;
    segments(s).gt_drink_count = data(s).drink_annot_count;
end

save('segments', 'segments');
fprintf('Filter min point labels done\n');

