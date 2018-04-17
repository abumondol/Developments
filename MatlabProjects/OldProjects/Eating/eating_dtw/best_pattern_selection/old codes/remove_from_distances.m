function distances = remove_from_distances(distances, remove_list)    
    remove_count = size(remove_list, 1);
    ix = [];
    %fprintf('Removing from distances: ');
    for i=1:remove_count
        %fprintf(' %d', i);
        s = remove_list(i,1);
        rix = remove_list(i,2);
        for j=1:36
            d = distances(j).dist;
            if j == s                
                d(d(:,1)==rix, :) = [];
            end
            
            d(d(:,2)==s & d(:,3)==rix, :) = [];
            
            distances(j).dist = d;
        end
    end
    
    %fprintf(' removing done\n');
        
end