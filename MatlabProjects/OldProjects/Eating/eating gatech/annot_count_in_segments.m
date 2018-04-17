function counts = annot_count_in_segments(segments, acl_annots)

    segment_count = size(segments, 1);
    counts = zeros(segment_count,1);
    for i = 1:segment_count
        s = segments(i,2);
        e = segments(i,3);
        counts(i) = sum(acl_annots(s:e, 2));
        if counts(i)>1
            counts(i)=1;
        end
    end
end