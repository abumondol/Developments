window_size = 30;
window_half = window_size/2;

windows = [];
for sub = 1:4    
    for sess = 1:6
        fprintf('%d, %d\n', sub, sess);        
        labels = data(sub).session(sess).labels;        
        count = length(labels);
        labels_left = labels(:,3)*1000 + labels(:, 4);  
        labels_right = labels(:,5)*1000 + labels(:, 6);  
        
        indices = [];
        activity = [];
        
        for s = 1: window_size : count - window_size+1            
            e = s + window_size - 1;
            m = (s + e - 1) / 2;
            
%             left = 0;
%             right = 0;                
%             if sum(labels(s:e, 3))~=0
%                 left = 1;
%             end
%             if sum(labels(s:e, 5))~=0
%                 right = 1;
%             end
%             ml = mode(labels(s:e, 7));            
%             ml_1 = find(activity_codes == ml) - 1;
            
            act_right = mode(labels_right(s:e, :));
            act_left  = mode(labels_left(s:e, :));            
            
            indices = [indices; s, e, m];
            activity = [activity; act_left, act_right];
        end
        
        windows(sub).session(sess).count = length(1: window_size : count - window_size+1);
        windows(sub).session(sess).indices = indices;
        windows(sub).session(sess).activity = activity;
        
    end
end

save('windows', 'windows');