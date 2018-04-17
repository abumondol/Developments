function [act_id, right, left] = find_mid_activity(activity_chunks, index)    
    c = activity_chunks;
    i = find(c(:,1)<=index & c(:,2)>=index);
    if isempty(i) || length(i)>1
        i
        fprintf('Problem in find mid activity, %d', index);        
        waitforbuttonpress
    end
    act_id =  c(i, 3);
    right = c(i, 4);
    left = c(i, 5);
end