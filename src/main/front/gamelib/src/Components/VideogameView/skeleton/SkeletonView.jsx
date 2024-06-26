import "./SkeletonView.css";

function SkeletonView() {
    return (
        <main className={"mainSkeleton"}>
            <div className={"backgroundSkeleton"}></div>
            <div className={"dataSkeleton"}>
                <div className={"coverSkeleton"}></div>
                <div className={"moreDataSkeleton"}></div>
                <div className={"newsSkeleton"}></div>
            </div>
        </main>
    );
}

export default SkeletonView;
