sub_count = length(data)

seqs = [];
for s= 1:sub_count
    seqs = [seqs; sequence(s).seq];
end
all_seqs = seqs;

seqs = seqs(seqs(:, 9)>0 & seqs(:, 9)<400, 1);
u = unique(seqs);
counts = zeros(length(u), 1);

for i=1:length(u)
    counts(i) = sum(seqs == u(i));
end

res = [counts, u];
res = sortrows(res);
res = [res(:,2), res(:,1)];
res = flipud(res);
res = [res, res(:,2)];

for i=2:length(all_seqs)-1
    if all_seqs(i, 9)>0 && all_seqs(i, 9)<400
        %p = find(res(:,1)==all_seqs(i-1,1));
        c = find(res(:,1)==all_seqs(i,1));       
        n = find(res(:,1)==all_seqs(i+1,1));
        
        res(c, 3) = res(c, 3) - 1;            
        %if ~isempty(p)>0 && p<c
        %    c = p;
        %end
           
        if ~isempty(n)>0 && n<c
            c = c;
        end
            
        res(c, 3) = res(c, 3) + 1;            
        
    end
end

res = [res, ico(4).vertices(res(:,1), :)];
sum(res(:,2))
sum(res(1:87, 2))