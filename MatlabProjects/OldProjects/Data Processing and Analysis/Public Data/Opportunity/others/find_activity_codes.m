activity_codes = [];

for sub = 1:4    
    for sess = 1:6
        labels = data(sub).session(sess).labels;       
        %labels = labels(:, 3)*1000 + labels(:, 4);
        labels = labels(:, 7);
        
        count = length(labels);                    
        activity = labels(1);
        activity_codes = [activity_codes; activity];
        for i =2:count
            if labels(i) ~= activity                
                activity = labels(i);                
                activity_codes = [activity_codes; activity];
            end                    
        end       
        
    end
end

activity_codes = unique(activity_codes);
save('activity_codes', 'activity_codes');