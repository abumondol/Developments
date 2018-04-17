sub_count = length(data);

s = zeros(sub_count, 4);
pos_vars=[];
neg_vars = [];
total_segments = 0;
total_pos = 0;
fneg = 0;
fpos = 0;
for sub = 1:sub_count
    seg = segments(sub).segments;
    s(sub,1) = size(segments(sub).annot_covered, 1);
    s(sub,2) =  segments(sub).annot_covered_count;
    s(sub,3) =  segments(sub).annot_covered_count_2;
    s(sub, 4) = sum(seg(:,2)<=-2.5 & seg(:,end)>=1);
    total_segments = total_segments + size(seg,1);
    total_pos = total_pos + sum(seg(:,end)>=1);
    
    seg = seg(seg(:,2)<=-3 & seg(:,3)>=0.2, :);
    pos_vars= [pos_vars; seg(seg(:,end)>=1, 3)];
    neg_vars= [neg_vars; seg(seg(:,end)==0, 3)];
end
[total_segments-total_pos, total_pos]
[length(neg_vars), length(pos_vars)]


a = sum(s)
a(2)/a(1)
a(4)/a(1)
edges = 0:0.01:5;
%h= [edges(2:end);cumsum(histcounts(pos_vars, edges)); cumsum(histcounts(neg_vars, edges))]';


