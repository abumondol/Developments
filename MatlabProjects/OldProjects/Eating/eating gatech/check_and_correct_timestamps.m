function acl = check_and_correct_timestamps(acl)
    
    t = acl(:,1);
    count = length(t);
    for j= 2:count
        if t(j) < t(j-1)
            %fprintf('%d, %d, %f\n', i, j, t(j));
            acl(j,1) = acl(j-1,1);
        end
    end

end